const db = require('../config/database');

// Crear reseña
exports.crearResena = async (req, res) => {
  try {
    const { idUsuario, idProducto, calificacion, comentario } = req.body;
    
    if (!idUsuario || !idProducto || !calificacion) {
      return res.status(400).json({ error: 'Datos incompletos' });
    }

    if (calificacion < 1 || calificacion > 5) {
      return res.status(400).json({ error: 'La calificación debe estar entre 1 y 5' });
    }
    
    const sql = `
      INSERT INTO resenas (
        id_usuario, id_producto, calificacion, comentario
      ) VALUES (
        :id_usuario, :id_producto, :calificacion, :comentario
      )
    `;
    
    await db.execute(sql, {
      id_usuario: idUsuario,
      id_producto: idProducto,
      calificacion: calificacion,
      comentario: comentario || null
    });
    
    res.status(201).json({ 
      message: 'Reseña creada exitosamente',
      calificacion: calificacion
    });
  } catch (err) {
    console.error('Error al crear reseña:', err);
    
    if (err.errorNum === 1) {
      return res.status(409).json({ 
        error: 'Ya has reseñado este producto anteriormente' 
      });
    }
    
    res.status(500).json({ 
      error: 'Error al crear reseña',
      details: err.message 
    });
  }
};

// Obtener reseñas por producto
exports.getResenasPorProducto = async (req, res) => {
  try {
    const { idProducto } = req.params;
    
    const sql = `
      SELECT 
        r.id_resena,
        r.calificacion,
        r.comentario,
        r.fecha_resena,
        u.nombre || ' ' || u.apellido AS nombre_usuario,
        u.email
      FROM resenas r
      JOIN usuarios u ON r.id_usuario = u.id_usuario
      WHERE r.id_producto = :id_producto
        AND r.estado_resena = 'ACTIVO'
      ORDER BY r.fecha_resena DESC
    `;
    
    const result = await db.execute(sql, { id_producto: idProducto });
    
    // Calcular promedio
    const sqlPromedio = `
      SELECT 
        ROUND(AVG(calificacion), 1) AS promedio_calificacion,
        COUNT(*) AS total_resenas
      FROM resenas
      WHERE id_producto = :id_producto
        AND estado_resena = 'ACTIVO'
    `;
    
    const resultPromedio = await db.execute(sqlPromedio, { id_producto: idProducto });
    
    res.json({
      resenas: result.rows,
      estadisticas: resultPromedio.rows[0]
    });
  } catch (err) {
    console.error('Error al obtener reseñas:', err);
    res.status(500).json({ 
      error: 'Error al obtener reseñas',
      details: err.message 
    });
  }
};

// Obtener todas las reseñas
exports.getTodasResenas = async (req, res) => {
  try {
    const sql = `
      SELECT 
        r.id_resena,
        r.calificacion,
        r.comentario,
        r.fecha_resena,
        u.nombre || ' ' || u.apellido AS nombre_usuario,
        pr.nombre_producto,
        pr.codigo_producto,
        c.nombre_categoria
      FROM resenas r
      JOIN usuarios u ON r.id_usuario = u.id_usuario
      JOIN productos pr ON r.id_producto = pr.id_producto
      JOIN categorias c ON pr.id_categoria = c.id_categoria
      WHERE r.estado_resena = 'ACTIVO'
      ORDER BY r.fecha_resena DESC
    `;
    
    const result = await db.execute(sql);
    res.json(result.rows);
  } catch (err) {
    console.error('Error al obtener reseñas:', err);
    res.status(500).json({ 
      error: 'Error al obtener reseñas',
      details: err.message 
    });
  }
};

// Obtener reseñas por usuario
exports.getResenasPorUsuario = async (req, res) => {
  try {
    const { idUsuario } = req.params;
    
    const sql = `
      SELECT 
        r.id_resena,
        r.calificacion,
        r.comentario,
        r.fecha_resena,
        pr.nombre_producto,
        pr.codigo_producto,
        c.nombre_categoria
      FROM resenas r
      JOIN productos pr ON r.id_producto = pr.id_producto
      JOIN categorias c ON pr.id_categoria = c.id_categoria
      WHERE r.id_usuario = :id_usuario
        AND r.estado_resena = 'ACTIVO'
      ORDER BY r.fecha_resena DESC
    `;
    
    const result = await db.execute(sql, { id_usuario: idUsuario });
    res.json(result.rows);
  } catch (err) {
    console.error('Error al obtener reseñas del usuario:', err);
    res.status(500).json({ 
      error: 'Error al obtener reseñas del usuario',
      details: err.message 
    });
  }
};

// Actualizar reseña
exports.actualizarResena = async (req, res) => {
  try {
    const { idResena } = req.params;
    const { calificacion, comentario } = req.body;
    
    if (calificacion && (calificacion < 1 || calificacion > 5)) {
      return res.status(400).json({ error: 'La calificación debe estar entre 1 y 5' });
    }
    
    const sql = `
      UPDATE resenas
      SET calificacion = NVL(:calificacion, calificacion),
          comentario = NVL(:comentario, comentario)
      WHERE id_resena = :id_resena
    `;
    
    await db.execute(sql, {
      calificacion: calificacion || null,
      comentario: comentario || null,
      id_resena: idResena
    });
    
    res.json({ message: 'Reseña actualizada correctamente' });
  } catch (err) {
    console.error('Error al actualizar reseña:', err);
    res.status(500).json({ 
      error: 'Error al actualizar reseña',
      details: err.message 
    });
  }
};

// Eliminar reseña (soft delete)
exports.eliminarResena = async (req, res) => {
  try {
    const { idResena } = req.params;
    
    const sql = `
      UPDATE resenas
      SET estado_resena = 'INACTIVO'
      WHERE id_resena = :id_resena
    `;
    
    await db.execute(sql, { id_resena: idResena });
    
    res.json({ message: 'Reseña eliminada correctamente' });
  } catch (err) {
    console.error('Error al eliminar reseña:', err);
    res.status(500).json({ 
      error: 'Error al eliminar reseña',
      details: err.message 
    });
  }
};