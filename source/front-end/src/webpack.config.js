module.exports = {
    mode: 'development',
    // context: __dirname + "/game",
    // entry: "./game/Main.js",
    entry: "./game/index.ts",
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            }
        ]
    },
    output: {
        // path: __dirname + "/dist",
        filename: "bundle.js"
    },
    watch: false
}