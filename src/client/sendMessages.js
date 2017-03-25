'use strict';
/* This declares the function that will send a message
 */

function sendMessage(e) {
  e.preventDefault();
  const inputNode = e.target.message;
  const message = inputNode.value + '\n';
  console.log("Sending message:", message);
  // Clear node
  inputNode.value = '';

  // Send the message
  const xhr = new XMLHttpRequest();
  xhr.onload = (e) => {
    if (xhr.status !== 200) {
      return window.alert("Error sending message to server");
    }
  }

  xhr.addEventListener("error", (e) => {
    console.log(e);
  });

  xhr.open("POST", config.hostUrl + '?id='+ config.id.toString());
  xhr.setRequestHeader("Content-type", "text/plain");
  xhr.send(message);
}

function initializeForm() {
  const form = document.getElementById('message-form');
  form.addEventListener('submit', sendMessage);
}

initializeForm();
