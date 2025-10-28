const oracledb = require('oracledb');
const db = require('./config/database');

async function testConnection() {
  try {
    console.log('🧪 Iniciando prueba de conexión a Oracle Cloud...\n');
    
    // Inicializar pool
    await db.initializePool();
    
    // Test 1: Consulta simple
    console.log('📋 Test 1: Consulta simple');
    const result1 = await db.execute('SELECT SYSDATE FROM DUAL');
    console.log('✅ Fecha servidor:', result1.rows[0]);
    
    // Test 2: Contar tablas
    console.log('\n📋 Test 2: Verificar tablas');
    const result2 = await db.execute(`
      SELECT table_name 
      FROM user_tables 
      WHERE table_name IN ('USUARIOS', 'PRODUCTOS', 'CATEGORIAS', 'PEDIDOS')
      ORDER BY table_name
    `);
    console.log('✅ Tablas encontradas:', result2.rows.length);
    result2.rows.forEach(row => console.log('  -', row.TABLE_NAME));
    
    // Test 3: Contar registros
    console.log('\n📋 Test 3: Contar registros');
    const result3 = await db.execute('SELECT COUNT(*) AS total FROM productos');
    console.log('✅ Total productos:', result3.rows[0].TOTAL);
    
    const result4 = await db.execute('SELECT COUNT(*) AS total FROM categorias');
    console.log('✅ Total categorías:', result4.rows[0].TOTAL);
    
    const result5 = await db.execute('SELECT COUNT(*) AS total FROM usuarios');
    console.log('✅ Total usuarios:', result5.rows[0].TOTAL);
    
    // Test 4: Obtener productos
    console.log('\n📋 Test 4: Primeros 5 productos');
    const result6 = await db.execute(`
      SELECT codigo_producto, nombre_producto, precio 
      FROM productos 
      WHERE ROWNUM <= 5
      ORDER BY nombre_producto
    `);
    
    result6.rows.forEach(prod => {
      console.log(`  - ${prod.CODIGO_PRODUCTO}: ${prod.NOMBRE_PRODUCTO} ($${prod.PRECIO})`);
    });
    
    // Test 5: Verificar paquete PL/SQL
    console.log('\n📋 Test 5: Verificar paquete pkg_levelup_gamer');
    const result7 = await db.execute(`
      SELECT object_name, object_type, status 
      FROM user_objects 
      WHERE object_name = 'PKG_LEVELUP_GAMER'
    `);
    
    if (result7.rows.length > 0) {
      console.log('✅ Paquete encontrado:', result7.rows[0]);
    } else {
      console.log('⚠️  Paquete no encontrado. Ejecuta el script SQL completo.');
    }
    
    console.log('\n✅ Todas las pruebas completadas exitosamente');
    
    await db.closePool();
    process.exit(0);
    
  } catch (err) {
    console.error('\n❌ Error en las pruebas:', err);
    process.exit(1);
  }
}

testConnection();