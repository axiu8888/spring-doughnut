<!-- src/App.vue -->
<template>
  <div id="app">
    <ChatRoom />
  </div>
</template>

<script>
import ChatRoom from './components/ChatRoom.vue'

export default {
  name: 'App',
  components: {
    ChatRoom
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: #ecf0f1;
}

</style>