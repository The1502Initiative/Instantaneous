# Instantaneous
Welcome to Instantaneous, an instant messaging app written by Emil Goldsmith Olesen and Simon Seo for their Computer Networks class. Back-end is written in plain java, with the server and HTTP protocols implemented directly.

The client has only been tested on Chrome on Ubuntu and MacOS so on other platforms and browsers uninteded behaviour could occur.

# How to run

## Server
compile the chatServer.java entrypoint by running

`javac src/server/chatServer.java`

or an equivalent compilation message, following this the server can be started with

`java src/server/chatServer`

## Client
You should now find your local IP Address and first enter this into the file `src/client/static/getConfig.js` in the object `config` in the key `hostUrl` in place of the default value `https://server.instantaneous.world` (remember to include the protocol in the URL).

When this is done you can either run the client locally by running `src/client/static/index.html` with your favourite browser (though we recommend using Chrome as this is what we tested with) or you can server the client with a simple node.js/express script written by running

```
cd src/client
npm install
node server.js
```

assuming you have npm and node installed.


