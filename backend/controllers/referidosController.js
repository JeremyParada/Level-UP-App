const db = require('../config/database');

// Registrar un nuevo referido
exports.registrarReferido = async (req, res) => {
  const { id_usuario_referidor, email_referido } = req.body;

  try {
    // Validar que el usuario referidor exista
    const referidor = await db.execute(
      `SELECT id_usuario FROM usuarios WHERE id_usuario = :id_usuario_referidor`,
      [id_usuario_referidor]
    );

    if (referidor.rows.length === 0) {
      return res.status(404).json({ message: 'Usuario referidor no encontrado' });
    }

    // Validar que el email del referido no exista ya como usuario
    const referidoExistente = await db.execute(
      `SELECT id_usuario FROM usuarios WHERE email = :email_referido`,
      [email_referido]
    );

    if (referidoExistente.rows.length > 0) {
      return res.status(400).json({ message: 'El referido ya está registrado como usuario' });
    }

    // Insertar el referido en la tabla
    await db.execute(
      `INSERT INTO referidos (id_usuario_referidor, codigo_referido, estado_referido)
       VALUES (:id_usuario_referidor, 'PENDIENTE', 'PENDIENTE')`,
      [id_usuario_referidor]
    );

    res.status(201).json({ message: 'Referido registrado exitosamente' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al registrar el referido' });
  }
};

// Consultar referidos de un usuario
exports.obtenerReferidos = async (req, res) => {
  const { id_usuario } = req.params;

  try {
    const referidos = await db.execute(
      `SELECT r.id_referido, r.codigo_referido, r.estado_referido, r.fecha_referido, r.puntos_otorgados
       FROM referidos r
       WHERE r.id_usuario_referidor = :id_usuario`,
      [id_usuario]
    );

    res.status(200).json(referidos.rows);
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener los referidos' });
  }
};

// Completar un referido y otorgar puntos
exports.completarReferido = async (req, res) => {
  const { id_referido, puntos } = req.body;

  try {
    // Validar que el referido exista
    const referido = await db.execute(
      `SELECT id_usuario_referidor FROM referidos WHERE id_referido = :id_referido`,
      [id_referido]
    );

    if (referido.rows.length === 0) {
      return res.status(404).json({ message: 'Referido no encontrado' });
    }

    const id_usuario_referidor = referido.rows[0].ID_USUARIO_REFERIDOR;

    // Actualizar el estado del referido y otorgar puntos
    await db.execute(
      `UPDATE referidos
       SET estado_referido = 'COMPLETADO', puntos_otorgados = :puntos
       WHERE id_referido = :id_referido`,
      [puntos, id_referido]
    );

    // Actualizar los puntos del usuario referidor
    await db.execute(
      `UPDATE usuarios
       SET puntos_levelup = puntos_levelup + :puntos
       WHERE id_usuario = :id_usuario_referidor`,
      [puntos, id_usuario_referidor]
    );

    res.status(200).json({ message: 'Referido completado y puntos otorgados' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al completar el referido' });
  }
};