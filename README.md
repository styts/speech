# Speech

An app that can currently visualize live audio data from the microphone.

Start the cljs/css watching process and open http://localhost:3000

    boot frontend

Connect to a Clojure repl, open user.clj and run to start the webserver and microphone capture components: 

    (set-init! #'dev-system)
    (reset)

You should see a graph of live microphone data coming in through the websocket. Currently has some lagging.
