var stompClient = null;
var notificationCount = 0;

//set lobbyId for testing
var lobbyId = 1;

$(document).ready(function() {
    console.log("Index page is ready");
    connect();

    $("#send").click(function() {
        sendMessage();
    });

    $("#startGame").click(function() {
        startGame();
    });

    $("#ready").click(function() {
        readycheck();
    });

    $("#nightaction").click(function() {
        nightaction();
    });

    $("#voting").click(function() {
        voting();
    });

    /*
    $("#send-private").click(function() {
        sendPrivateMessage();
    });
    */
    $("#notifications").click(function() {
        resetNotificationCount();
    });
});

function connect() {
    var socket = new SockJS('/game');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        updateNotificationDisplay();
        stompClient.subscribe(`/topic/lobby/${lobbyId}`, function (message) {
            showMessage(JSON.parse(message.body).content);
        });
        /*
        stompClient.subscribe('/user/topic/private-messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe('/topic/global-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });

        stompClient.subscribe('/user/topic/private-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });
        */
    });
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function sendMessage() {
    console.log("sending message");
    stompClient.send("/app/test", {}, JSON.stringify({'username': $("#username").val(), 'selection': $("#selection").val()}));
}

function startGame() {
    lobbyId = parseInt($("#lobbyId").val(), 10);
    console.log("start game: ", lobbyId);
    stompClient.send("/app/startgame", {}, JSON.stringify({'lobbyId': lobbyId}));
}

function readycheck() {
    console.log("sending readycheck");
    stompClient.send("/app/ready", {}, JSON.stringify({'username': $("#username").val(), 'gameState': $("#gameState").val()}));
}

function nightaction () {
    var path = "/app/" + $("#roleName").val() + "/nightaction"
    console.log("path: ", path);
    stompClient.send(path, {}, JSON.stringify({'username': $("#username").val(), 'selection': $("#selection").val()}));
}

function voting () {
    stompClient.send("/app/voting", {}, JSON.stringify({'username': $("#username").val(), 'selection': $("#selection").val()}));
}

/*
function sendPrivateMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message", {}, JSON.stringify({'messageContent': $("#private-message").val()}));
}
*/

function updateNotificationDisplay() {
    if (notificationCount == 0) {
        $('#notifications').hide();
    } else {
        $('#notifications').show();
        $('#notifications').text(notificationCount);
    }
}

function resetNotificationCount() {
    notificationCount = 0;
    updateNotificationDisplay();
}