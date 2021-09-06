console.log(config.devServer)
config.devServer = Object.assign(
    {},
    config.devServer || {},
    {
        open: false,
        proxy: { "/api": "http://localhost:8081" }
    }
)