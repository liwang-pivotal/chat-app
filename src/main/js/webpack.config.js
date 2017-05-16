var path = require('path');
var webpack = require('webpack');

const PATHS = {
    build: path.join(__dirname, 'build')
};

module.exports = {

    entry: './server.js',

    output: {
        path: PATHS.build,
        filename: 'ui-bundle.js'
    },

    module: {
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: 'babel-loader',
                query: {
                    presets: ['es2015']
                }
            }
        ],
    }
}
