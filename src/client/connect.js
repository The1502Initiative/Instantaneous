'use strict';
/* Connect to the server to tell them we're online
 * and to receive our client ID. Following that we start
 * pinging the server for updates on received messages.
 */

function parseMessages(messageArray) {
  const messageContainer = document.getElementById("messages");
  messageArray.forEach((message) => {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");
    if (messageElement.id === config.id) {
      messageElement.classList.add("my-message");
    }
    else {
      messageElement.classList.add("their-message");
    }

    const text = document.createTextNode(message.text);
    messageElement.appendChild(text);

    // Add the message
    messageContainer.appendChild(messageElement);
  });
}

function pingForUpdates() {
  const xhr = new XMLHttpRequest();
  xhr.onload = (e) => {
    const response = xhr.response;
    console.log(response);
    if (response.messages) {
      parseMessages(response.messages);
    }
  };
  xhr.responseType = "json";
  xhr.open("GET", config.hostUrl);
  xhr.send();
}

function connect() {
  const xhr = new XMLHttpRequest();
  xhr.onload = (e) => {
    const response = xhr.response;
    console.log(response);
    // Remove the connecting message
    const connectingMessage = document.getElementById("connecting");
    connectingMessage.remove();
    // Start pinging for updates
    setInterval(pingForUpdates, 500);
  };
  xhr.responseType = "json";
  xhr.open("GET", config.hostUrl);
  xhr.send();
}

connect();
