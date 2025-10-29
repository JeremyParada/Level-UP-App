const express = require('express');
const router = express.Router();
const pedidosController = require('../controllers/pedidosController');

router.post('/', pedidosController.crearPedido);
router.get('/usuario/:idUsuario', pedidosController.getHistorialPedidos);
router.get('/:idPedido', pedidosController.getDetallePedido);
router.put('/:idPedido/estado', pedidosController.actualizarEstadoPedido);
router.put('/:idPedido/cancelar', pedidosController.cancelarPedido);

module.exports = router;