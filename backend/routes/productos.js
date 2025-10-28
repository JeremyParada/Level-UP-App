const express = require('express');
const router = express.Router();
const productosController = require('../controllers/productosController');

// Rutas de productos
router.get('/', productosController.getProductos);
router.get('/categorias', productosController.getCategorias);
router.get('/categoria/:idCategoria', productosController.getProductosPorCategoria);
router.get('/:codigo', productosController.getProductoPorCodigo);

module.exports = router;