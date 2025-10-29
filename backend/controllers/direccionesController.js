const db = require('../config/database');
const oracledb = require('oracledb'); // Asegúrate de importar oracledb

// Obtener direcciones de un usuario
exports.getDireccionesPorUsuario = async (req, res) => {
  const { idUsuario } = req.params;

  try {
    const sql = `
      SELECT 
        id_direccion,
        tipo_direccion,
        calle,
        numero,
        comuna,
        ciudad,
        region,
        codigo_postal,
        es_principal
      FROM direcciones
      WHERE id_usuario = :idUsuario
    `;

    const result = await db.execute(sql, { idUsuario }, { outFormat: oracledb.OBJECT });
    res.json(result.rows);
  } catch (err) {
    console.error('Error al obtener direcciones:', err);
    res.status(500).json({ error: 'Error al obtener direcciones' });
  }
};

// Crear una nueva dirección
exports.createDireccion = async (req, res) => {
  const { idUsuario, tipoDireccion, calle, numero, comuna, ciudad, region, codigoPostal, esPrincipal } = req.body;

  try {
    const sql = `
      INSERT INTO direcciones (
        id_usuario, tipo_direccion, calle, numero, comuna, ciudad, region, codigo_postal, es_principal
      ) VALUES (
        :idUsuario, :tipoDireccion, :calle, :numero, :comuna, :ciudad, :region, :codigoPostal, :esPrincipal
      )
    `;

    await db.execute(sql, { idUsuario, tipoDireccion, calle, numero, comuna, ciudad, region, codigoPostal, esPrincipal });
    res.status(201).json({ message: 'Dirección creada exitosamente' });
  } catch (err) {
    console.error('Error al crear dirección:', err);
    res.status(500).json({ error: 'Error al crear dirección' });
  }
};