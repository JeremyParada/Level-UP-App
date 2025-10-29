const express = require('express');
const router = express.Router();
const carritoController = require('../controllers/carritoController');

// Obtener carrito del usuario
router.get('/usuario/:idUsuario', carritoController.getCarrito);

// Agregar producto al carrito
router.post('/', carritoController.agregarAlCarrito);

// Actualizar cantidad de producto en el carrito
router.put('/item/:idDetalleCarrito', carritoController.actualizarCantidadCarrito);

// Eliminar producto del carrito
router.delete('/item/:idDetalleCarrito', carritoController.eliminarProductoCarrito);

// Vaciar carrito completo
router.delete('/usuario/:idUsuario', carritoController.vaciarCarrito);

module.exports = router;