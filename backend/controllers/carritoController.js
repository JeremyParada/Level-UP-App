const db = require('../config/database');
const oracledb = require('oracledb');

// Obtener carrito del usuario
exports.getCarrito = async (req, res) => {
  try {
    const { idUsuario } = req.params;
    
    const sql = `
      SELECT 
        c.id_carrito,
        dc.id_detalle_carrito,
        dc.id_producto,
        p.codigo_producto,
        p.nombre_producto,
        p.precio,
        dc.cantidad,
        (p.precio * dc.cantidad) AS subtotal
      FROM carrito c
      JOIN detalle_carrito dc ON c.id_carrito = dc.id_carrito
      JOIN productos p ON dc.id_producto = p.id_producto
      WHERE c.id_usuario = :id_usuario
        AND c.estado_carrito = 'ACTIVO'
    `;
    
    const result = await db.execute(sql, { id_usuario: idUsuario });
    res.json(result.rows);
  } catch (err) {
    console.error('Error al obtener carrito:', err);
    res.status(500).json({ 
      error: 'Error al obtener carrito',
      details: err.message 
    });
  }
};

// Agregar producto al carrito
exports.agregarAlCarrito = async (req, res) => {
  try {
    const { idUsuario, idProducto, cantidad } = req.body;

    // Verificar si existe carrito activo
    let idCarrito;
    const sqlCarrito = `
      SELECT id_carrito 
      FROM carrito 
      WHERE id_usuario = :id_usuario 
        AND estado_carrito = 'ACTIVO'
    `;
    const resultCarrito = await db.execute(sqlCarrito, { id_usuario: idUsuario });

    if (resultCarrito.rows.length === 0) {
      // Crear nuevo carrito
      const sqlNuevoCarrito = `
        INSERT INTO carrito (id_usuario)
        VALUES (:id_usuario)
        RETURNING id_carrito INTO :id_carrito
      `;
      const resultNuevo = await db.execute(sqlNuevoCarrito, {
        id_usuario: idUsuario,
        id_carrito: { type: oracledb.NUMBER, dir: oracledb.BIND_OUT }
      });
      idCarrito = resultNuevo.outBinds.id_carrito[0];
    } else {
      idCarrito = resultCarrito.rows[0].ID_CARRITO;
    }

    // Verificar si el producto ya está en el carrito
    const sqlVerificar = `
      SELECT id_detalle_carrito, cantidad
      FROM detalle_carrito
      WHERE id_carrito = :id_carrito
        AND id_producto = :id_producto
    `;
    const resultVerificar = await db.execute(sqlVerificar, {
      id_carrito: idCarrito,
      id_producto: idProducto
    });

    if (resultVerificar.rows.length > 0) {
      // Actualizar cantidad existente
      const nuevaCantidad = resultVerificar.rows[0].CANTIDAD + cantidad;
      const sqlActualizar = `
        UPDATE detalle_carrito
        SET cantidad = :cantidad
        WHERE id_detalle_carrito = :id_detalle
      `;
      await db.execute(sqlActualizar, {
        cantidad: nuevaCantidad,
        id_detalle: resultVerificar.rows[0].ID_DETALLE_CARRITO
      });
    } else {
      // Agregar nuevo producto al carrito
      const sqlDetalle = `
        INSERT INTO detalle_carrito (id_carrito, id_producto, cantidad, precio_unitario)
        VALUES (:id_carrito, :id_producto, :cantidad, 
          (SELECT precio FROM productos WHERE id_producto = :id_producto))
      `;
      await db.execute(sqlDetalle, {
        id_carrito: idCarrito,
        id_producto: idProducto,
        cantidad
      });
    }

    res.status(201).json({ message: 'Producto agregado al carrito' });
  } catch (err) {
    console.error('Error al agregar al carrito:', err);
    res.status(500).json({ error: 'Error al agregar al carrito', details: err.message });
  }
};

// Actualizar cantidad de producto en el carrito
exports.actualizarCantidadCarrito = async (req, res) => {
  try {
    const { idDetalleCarrito } = req.params;
    const { cantidad } = req.body;
    
    if (cantidad < 1) {
      return res.status(400).json({ error: 'La cantidad debe ser al menos 1' });
    }
    
    const sql = `
      UPDATE detalle_carrito
      SET cantidad = :cantidad
      WHERE id_detalle_carrito = :id_detalle_carrito
    `;
    
    await db.execute(sql, {
      cantidad,
      id_detalle_carrito: idDetalleCarrito
    });
    
    res.json({ message: 'Cantidad actualizada correctamente' });
  } catch (err) {
    console.error('Error al actualizar cantidad:', err);
    res.status(500).json({ 
      error: 'Error al actualizar cantidad',
      details: err.message 
    });
  }
};

// Eliminar producto del carrito
exports.eliminarProductoCarrito = async (req, res) => {
  try {
    const { idDetalleCarrito } = req.params;
    
    const sql = `
      DELETE FROM detalle_carrito
      WHERE id_detalle_carrito = :id_detalle_carrito
    `;
    
    await db.execute(sql, { id_detalle_carrito: idDetalleCarrito });
    
    res.json({ message: 'Producto eliminado del carrito' });
  } catch (err) {
    console.error('Error al eliminar producto:', err);
    res.status(500).json({ 
      error: 'Error al eliminar producto del carrito',
      details: err.message 
    });
  }
};

// Vaciar carrito completo
exports.vaciarCarrito = async (req, res) => {
  try {
    const { idUsuario } = req.params;
    
    // Obtener ID del carrito activo
    const sqlCarrito = `
      SELECT id_carrito
      FROM carrito
      WHERE id_usuario = :id_usuario
        AND estado_carrito = 'ACTIVO'
    `;
    
    const resultCarrito = await db.execute(sqlCarrito, { id_usuario: idUsuario });
    
    if (resultCarrito.rows.length === 0) {
      return res.status(404).json({ error: 'No hay carrito activo' });
    }
    
    const idCarrito = resultCarrito.rows[0].ID_CARRITO;
    
    // Eliminar todos los productos del carrito
    const sqlEliminar = `
      DELETE FROM detalle_carrito
      WHERE id_carrito = :id_carrito
    `;
    
    await db.execute(sqlEliminar, { id_carrito: idCarrito });
    
    res.json({ message: 'Carrito vaciado correctamente' });
  } catch (err) {
    console.error('Error al vaciar carrito:', err);
    res.status(500).json({ 
      error: 'Error al vaciar carrito',
      details: err.message 
    });
  }
};