// sql-asm.js contiene rutas de Node que no se utilizan en el navegador.
config.resolve = config.resolve || {};
config.resolve.fallback = Object.assign({}, config.resolve.fallback || {}, { fs: false, path: false, crypto: false });
