'use strict';
/* Connect to the server to tell them we're online
 * and to receive our client ID. Following that we start
 * pinging the server for updates on received messages.
 */

function parseMessages(messageArray) {
  const messageContainer = document.getElementById("messages");
  messageArray.forEach((message) => {
    const messageElement = document.createElement("p");
    messageElement.classList.add("message");

    const text = document.createTextNode(message.text);
    messageElement.appendChild(text);

    const messageWrapper = document.createElement("div");
    messageWrapper.classList.add("message-wrapper");

    if (message.id === config.id) {
      messageWrapper.classList.add("my-message");
    }
    else {
      messageWrapper.classList.add("their-message");
    }

    const senderElement = document.createElement("span");
    const sender = document.createTextNode(message.id);
    senderElement.appendChild(sender);

    // Add the message and writer
    messageWrapper.appendChild(senderElement);
    messageWrapper.appendChild(document.createElement("br"));
    messageWrapper.appendChild(messageElement);
    messageContainer.appendChild(messageWrapper);
  });
}

function pingForUpdates() {
  const xhr = new XMLHttpRequest();

  xhr.addEventListener("error", (e) => {
    console.log(e);
  });

  xhr.onload = (e) => {
    if (xhr.status !== 200) {
      return window.alert("There was an error connecting to the server");
    }

    const response = xhr.response;
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

  xhr.addEventListener("error", (e) => {
    console.log(e);
  });

  xhr.onload = (e) => {
    if (xhr.status !== 200) {
      return window.alert("An arrow occured connecting to the server, sorry");
    }
    const response = xhr.response;
    // Set id
    config.id = response.id;
    if (!config.id) {
      console.log("No id was returned");
      // Try connecting again
      return connect();
    }
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

const testMessages = [{id: "Emil Goldsmith Olesen", text:"Whathsdajflksahflksahflksahflkahflkahflkahflkahfahflahflkahflkahflahflkahflahflahflafhdlkj's up you hot piece of ass?"}, {id: "Simon Seo", text:"Whatwadhfaskdhlkhdlkjsahfdlkahfdlahflksahflksahflksahfkjdsahfkjdsalhfdlksahflksahfahflksahfdlahflahflafdhdhka the hell are you saying you offensive cunt?"}];
setTimeout(parseMessages.bind(null, testMessages), 1*1000);
