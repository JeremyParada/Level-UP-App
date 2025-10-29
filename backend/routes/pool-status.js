const express = require('express');
const router = express.Router();
const oracledb = require('oracledb');

router.get('/pool-status', async (req, res) => {
  try {
    const pool = oracledb.getPool();
    res.json({
      status: 'OK',
      poolStatus: pool.status,
      connectionsInUse: pool.connectionsInUse,
      connectionsOpen: pool.connectionsOpen
    });
  } catch (err) {
    res.status(500).json({ error: 'Error al obtener estado del pool', details: err.message });
  }
});

module.exports = router;