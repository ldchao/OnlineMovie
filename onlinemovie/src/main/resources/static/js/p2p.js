var signalingChannel, key, id,
    haveLocalMedia = false,
    weWaited = false,
    myVideoStream, myVideo,
    yourVideoStream, yourVideo,
    doNothing = function () {
    },
    pc,
    constraints = {
        mandatory: {
            OfferToReceiveAudio: true,
            OfferToReceiveVideo: true
        }
    };

// 自动开始获取本地媒体
window.onload = function () {
    myVideo = document.getElementById('myVideo');
    yourVideo = document.getElementById('yourVideo');
    getMedia();
    connect()
};


// 连接服务器并建立信令通道
function connect() {
    var errorCB, scHandlers, handleMsg;
    // 获取连接密钥
    key = document.getElementById('key').value;
    // 处理通过信令通道收到的所有消息
    handleMsg = function (msg) {
        // var msgE = document.getElementById('inmessages');

        var msgString = JSON.stringify(msg).replace(/\\r\\n/g, '\n');
        // msgE.value = msgString + '\n' + msgE.value;    // 将最新的消息放置在最上方
        console.log('msgString:', msgString);
        if (msg.type === 'offer') {
            pc.setRemoteDescription(new RTCSessionDescription(msg));
            answer()
        } else if (msg.type === 'answer') {
            pc.setRemoteDescription(new RTCSessionDescription(msg))
        } else if (msg.type === 'candidate') {
            pc.addIceCandidate(new RTCIceCandidate({
                sdpMLineIndex: msg.mlineindex,
                candidate: msg.candidate
            }))
        }
    };
    scHandlers = {
        'onWaiting': function () {
            setStatus('Waiting');
            weWaited = true
        },
        'onConnected': function () {
            setStatus('Connected');
            // 已成功连接，开始建立对等连接
            createPC()
        },
        'onMessage': handleMsg
    };
    // 创建信令通道
    signalingChannel = createSignalingChannel(key, scHandlers);
    errorCB = function (msg) {
        document.getElementById('response').innerHTML = msg
    };

    // 进行连接
    signalingChannel.connect(errorCB)
}

// 通过信道发送消息
function send(msg) {
    var handler = function (res) {
        document.getElementById('response').innerHTML = res;
    };
    msg = msg || document.getElementById('message').value;   // 没有消息则获取信道消息
    // 发布到屏幕上
    // msgE = document.getElementById('outmessages');
    // var msgString = JSON.stringify(msg).replace(/\\r\\n/g, '\n');
    // msgE.value = msgString + '\n' + msgE.value;
    // 通过信令通道发送出去
    signalingChannel.send(msg, handler)
}

// 获取本地媒体
function getMedia() {
    (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia)({
        audio: true,
        video: true
    }, gotUserMedia, didNotGetUserMedia)
}

function didNotGetUserMedia() {
    console.log('cound not get user media')
}

function gotUserMedia(stream) {
    myVideoStream = stream;
    haveLocalMedia = true;
    // 向我显示我的本地视频
    myVideo.srcObject = myVideoStream;
    // 等待pc创建完毕
    attachMediaIfReady()
}

// 创建对等连接，即实例化peerConnection
function createPC() {
    var stunUri = true,
        turnUri = false,
        config = [];

    if (stunUri) {
        config.push({
            url: 'stun:stunserver.org'
        })
    }
    if (turnUri) {
        if (stunUri) {
            config.push({
                url: 'turn:user@turn.webrtcbook.com',
                credential: 'test'
            })
        } else {
            config.push({
                url: 'turn:user@turn-only.webrtcbook.com',
                credential: 'test'
            })
        }
    }
    console.log('config: ', JSON.stringify(config));
    pc = new RTCPeerConnection({
        iceServers: config
    });
    pc.onicecandidate = onIceCandidate;
    pc.onaddstream = onRemoteStreamAdded;
    pc.onremovestream = onRemoteStreamRemoved;

    // 等待媒体就绪
    attachMediaIfReady()
}

// 如果当前浏览器有另一个候选项，将其发送给对等端
function onIceCandidate(e) {
    if (e.candidate) {
        send({
            type: 'candidate',
            mlineindex: e.candidate.sdpMLineIndex,
            candidate: e.candidate.candidate
        })
    }
}

// 如果我们浏览器检测到另一端加入了媒体流，则将其显示在屏幕上
function onRemoteStreamAdded(e) {
    yourVideoStream = e.stream;
    console.log('yourVideo: ', yourVideo);
    yourVideo.srcObject = yourVideoStream;
    setStatus('On call')
}

// 远端移除流，这里不做操作
function onRemoteStreamRemoved(e) {

}

function attachMediaIfReady() {
    if (pc && haveLocalMedia) attachMedia()
}

// 将本地流添加至对等连接
function attachMedia() {
    pc.addStream(myVideoStream);
    setStatus('Ready for call');
}

// 生成一个offer
function call() {
    pc.createOffer(gotDescription, doNothing, constraints)
}

// 应答会话描述，生成answer
function answer() {
    pc.createAnswer(gotDescription, doNothing, constraints)
}

// 一旦获取到了会话描述，就将其作为本地描述，然后将其发送至另一端的浏览器
function gotDescription(localDesc) {
    pc.setLocalDescription(localDesc);
    send(localDesc)
}

// 进度操作
function setStatus(str) {
    var statuslineE = document.getElementById('statusline'),
        statusE = document.getElementById('status'),
        sendE = document.getElementById('send'),
        connectE = document.getElementById('connect'),
        callE = document.getElementById('call'),
        scMessageE = document.getElementById('scMessage');
    var setup = document.getElementById('setup');
    switch (str) {
        case 'Waiting':
            statuslineE.style.display = 'inline';
            statusE.innerHTML = '等待对等连接...';
            sendE.style.display = 'none';
            connectE.style.display = 'none';
            break;
        case 'Connected':
            statuslineE.style.display = 'inline';
            statusE.innerHTML = '对等信道已连接，等待本地媒体....';
            sendE.style.display = 'inline';
            connectE.style.display = 'none';
            // scMessageE.style.display = 'inline-block';
            scMessageE.style.display = 'none';
            break;
        case 'Ready for call':
            // statusE.innerHTML = '准备呼叫...';
            // callE.style.display = 'inline';
            call();
            break;
        case 'On call':
            statusE.innerHTML = 'On call';
            callE.style.display = 'none';
            setup.style.display = 'none';
            break;
        default:
    }
}