const oracledb = require('oracledb');
const db = require('../config/database');
const fs = require('fs');
const path = require('path');

// Obtener todos los productos con su categoría
exports.getProductos = async (req, res) => {
  let connection;

  try {
    const sql = `
      SELECT 
        p.id_producto AS id,
        p.codigo_producto AS codigo,
        p.nombre_producto AS nombre,
        p.precio,
        p.descripcion,
        p.stock,
        c.nombre_categoria AS categoria
      FROM productos p
      JOIN categorias c ON p.id_categoria = c.id_categoria
      WHERE p.estado_producto = 'ACTIVO'
      ORDER BY p.nombre_producto
    `;

    connection = await oracledb.getConnection();

    const result = await connection.execute(sql, {}, { outFormat: oracledb.OBJECT });

    // Asociar imágenes a los productos
    const productos = result.rows.map((row) => {
      const codigo = row.CODIGO.toLowerCase();
      const imgPathJpg = path.join(__dirname, '../../build/assets/img', `${codigo}.jpg`);
      const imgPathJpeg = path.join(__dirname, '../../build/assets/img', `${codigo}.jpeg`);

      let imagen = null;
      if (fs.existsSync(imgPathJpg)) {
        imagen = `/assets/img/${codigo}.jpg`;
      } else if (fs.existsSync(imgPathJpeg)) {
        imagen = `/assets/img/${codigo}.jpeg`;
      } else {
        imagen = `/assets/img/default.jpg`; // Imagen por defecto si no se encuentra ninguna
      }

      return {
        id: row.ID,
        codigo: row.CODIGO,
        nombre: row.NOMBRE,
        precio: row.PRECIO,
        descripcion: row.DESCRIPCION || '',
        stock: row.STOCK,
        categoria: row.CATEGORIA,
        imagen,
      };
    });

    res.json(productos);
  } catch (err) {
    console.error('Error al obtener productos:', err);
    res.status(500).json({ error: 'Error al obtener productos', details: err.message });
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error('Error al cerrar conexión:', err);
      }
    }
  }
};

// Obtener producto por código
exports.getProductoPorCodigo = async (req, res) => {
  let connection;

  try {
    const { codigo } = req.params;

    const sql = `
      SELECT 
        p.id_producto AS id,
        p.codigo_producto AS codigo,
        p.nombre_producto AS nombre,
        p.precio,
        p.descripcion,
        p.stock,
        p.estado_producto AS estado,
        c.id_categoria AS id_categoria,
        c.nombre_categoria AS categoria
      FROM productos p
      JOIN categorias c ON p.id_categoria = c.id_categoria
      WHERE p.codigo_producto = :codigo
        AND p.estado_producto = 'ACTIVO'
    `;

    connection = await oracledb.getConnection();
    const result = await connection.execute(sql, { codigo }, { outFormat: oracledb.OBJECT });

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    const producto = result.rows[0];

    res.json({
      id: producto.ID,
      codigo: producto.CODIGO,
      nombre: producto.NOMBRE,
      precio: producto.PRECIO,
      descripcion: producto.DESCRIPCION || '',
      stock: producto.STOCK,
      estado: producto.ESTADO,
      idCategoria: producto.ID_CATEGORIA,
      categoria: producto.CATEGORIA,
      imagen: `/assets/img/${producto.CODIGO.toLowerCase()}.jpg`, // Ruta de la imagen
    });
  } catch (err) {
    console.error('Error al obtener producto:', err);
    res.status(500).json({
      error: 'Error al obtener producto',
      details: err.message,
    });
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error('Error al cerrar conexión:', err);
      }
    }
  }
};

// Obtener todas las categorías con conteo de productos
exports.getCategorias = async (req, res) => {
  try {
    const sql = `
      SELECT 
        c.id_categoria,
        c.nombre_categoria,
        c.descripcion,
        COUNT(p.id_producto) AS total_productos
      FROM categorias c
      LEFT JOIN productos p ON c.id_categoria = p.id_categoria 
        AND p.estado_producto = 'ACTIVO'
      GROUP BY c.id_categoria, c.nombre_categoria, c.descripcion
      ORDER BY c.nombre_categoria
    `;

    const result = await db.execute(sql);
    res.json(result.rows);
  } catch (err) {
    console.error('Error al obtener categorías:', err);
    res.status(500).json({
      error: 'Error al obtener categorías',
      details: err.message
    });
  }
};

// Obtener productos por categoría
exports.getProductosPorCategoria = async (req, res) => {
  try {
    const { idCategoria } = req.params;

    const sql = `
      SELECT 
        p.id_producto,
        p.codigo_producto,
        p.nombre_producto,
        p.precio,
        p.descripcion,
        p.stock,
        c.nombre_categoria
      FROM productos p
      JOIN categorias c ON p.id_categoria = c.id_categoria
      WHERE p.id_categoria = :id_categoria
        AND p.estado_producto = 'ACTIVO'
      ORDER BY p.nombre_producto
    `;

    const result = await db.execute(sql, { id_categoria: idCategoria });
    res.json(result.rows);
  } catch (err) {
    console.error('Error al obtener productos por categoría:', err);
    res.status(500).json({
      error: 'Error al obtener productos',
      details: err.message
    });
  }
};