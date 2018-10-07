module.exports = {
    mode: 'development',
    // context: __dirname + "/game",
    entry: "./game/Main.js",
    output: {
        // path: __dirname + "/dist",
        filename: "bundle.js"
    },
    watch: true
}