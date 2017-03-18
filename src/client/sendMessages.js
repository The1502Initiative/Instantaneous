'use strict';
/* This declares the function that will send a message
 */

function sendMessage(e) {
  e.preventDefault();
  const inputNode = e.target.message;
  const message = inputNode.value;
  // Clear node
  inputNode.value = '';

  // Send the message
  const xhr = new XMLHttpRequest();
  xhr.onload = (e) => {
    const response = xhr.response;
    console.log(response);
  }

  xhr.open("POST", config.hostUrl);
  xhr.send(message);
}

function initializeForm() {
  const form = document.getElementsByTagName('form')[0];
  form.addEventListener('submit', sendMessage);
}

initializeForm();