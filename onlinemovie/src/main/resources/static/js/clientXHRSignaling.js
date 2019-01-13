var createSignalingChannel = function (key, handlers) {

    var id, status, socket,
        doNothing = function () {
        },
        initHandler = function (h) {
            return ((typeof h === 'function') && h) || doNothing;
        },
        waitingHandler = initHandler(handlers.onWaiting),
        connectedHandler = initHandler(handlers.onConnected),
        messageHandler = initHandler(handlers.onMessage);


    // Set up connection with signaling server
    function connect(failureCB) {
        failureCB = ((typeof failureCB === 'function') && failureCB) || doNothing;


        if(typeof(WebSocket) === "undefined") {
            console.log("您的浏览器不支持WebSocket");
        }else {
            console.log("您的浏览器支持WebSocket");

            socket = new WebSocket("ws://"+location.host+"/websocket/" + key);
            //打开事件
            socket.onopen = function () {
                status = "waiting";
                waitingHandler();
                // socket.send("这是来自客户端的消息" + location.href + new Date());
            };

            //获得消息事件
            socket.onmessage = function(msg) {
                console.log(msg.data);
                // $("#message").val(msg.data)
                //发现消息进入 开始处理前端触发逻辑

                var res = JSON.parse(msg.data);

                // if messages property exists, then we are connected
                if (status !== "connected") {
                    // switch status to connected since it is now!
                    status = "connected";
                    connectedHandler();
                }

                handleMessage(res)

            };

            //关闭事件
            socket.onclose = function() {
                console.log("Socket已关闭");
                // $("#close").val("Socket已关闭")
            };

            //发生了错误事件
            socket.onerror = function() {
                failureCB("Socket发生了错误");
            }

        }
    }

    // Schedule incoming messages for asynchronous handling.
    function handleMessage(msg) {   // process message asynchronously
        setTimeout(function () {
            messageHandler(msg);
        }, 0);
    }


    // Send a message to the other browser on the signaling channel
    function send(msg) {
        socket.send(JSON.stringify(msg));
    }


    return {
        connect: connect,
        send: send
    };

};
