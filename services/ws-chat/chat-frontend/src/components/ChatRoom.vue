<!-- src/components/ChatRoom.vue -->
<template>
  <div class="fullscreen-chat">
    <!-- 顶部状态栏 -->
    <div class="chat-status-bar">
      <div class="status-info">
        <span class="app-title">全屏聊天室</span>
        <span v-if="connected" class="user-info">欢迎, {{ username }}</span>
      </div>
      <div class="connection-status">
        <div v-if="connected" class="status-indicator connected">
          <span class="dot"></span>
          已连接 (在线用户: {{ onlineUsers }})
        </div>
        <div v-else class="status-indicator disconnected">
          <span class="dot"></span>
          未连接
        </div>
        <button v-if="connected" @click="disconnect" class="disconnect-btn">
          退出聊天
        </button>
      </div>
    </div>

    <!-- 登录界面 -->
    <div v-if="!connected" class="fullscreen-login">
      <div class="login-container">
        <h1 class="login-title">全屏聊天室</h1>
        <p class="login-subtitle">与朋友们实时聊天</p>
        
        <div class="login-form">
          <div class="input-group">
            <label for="username">用户名</label>
            <input 
              id="username"
              v-model="username" 
              type="text" 
              placeholder="请输入您的用户名"
              @keyup.enter="connect"
              class="login-input"
            />
          </div>
          <button @click="connect" class="login-btn">
            进入聊天室
          </button>
        </div>

        <div class="login-features">
          <div class="feature">
            <span class="feature-icon">💬</span>
            <span>实时消息</span>
          </div>
          <div class="feature">
            <span class="feature-icon">👥</span>
            <span>多人在线</span>
          </div>
          <div class="feature">
            <span class="feature-icon">🚀</span>
            <span>快速响应</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 聊天界面 -->
    <div v-else class="fullscreen-chat-room">
      <!-- 侧边栏 - 在线用户 -->
      <div class="chat-sidebar">
        <div class="sidebar-header">
          <h3>在线用户 ({{ onlineUsers }})</h3>
        </div>
        <div class="user-list">
          <div 
            v-for="user in userList" 
            :key="user.id"
            class="user-item"
          >
            <span class="user-avatar">{{ user.name.charAt(0) }}</span>
            <span class="user-name">{{ user.name }}</span>
          </div>
        </div>
      </div>

      <!-- 主聊天区域 -->
      <div class="chat-main">
        <!-- 消息区域 -->
        <div class="messages-container" ref="messagesContainer">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message', message.type.toLowerCase(), { 'own-message': message.sender === username }]"
          >
            <div v-if="message.type === 'JOIN' || message.type === 'LEAVE'" class="system-message">
              <span class="system-icon">📢</span>
              {{ message.sender }} {{ message.type === 'JOIN' ? '加入' : '离开' }}了聊天室
              <span class="message-time">{{ formatTime(message.timestamp) }}</span>
            </div>
            <div v-else class="user-message">
              <div class="message-header" v-if="message.sender !== username">
                <span class="message-sender">{{ message.sender }}</span>
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
              </div>
              <div class="message-content" :class="{ 'own-content': message.sender === username }">
                {{ message.content }}
              </div>
              <div class="message-header own-header" v-if="message.sender === username">
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-container">
          <div class="input-wrapper">
            <input 
              v-model="newMessage" 
              type="text" 
              placeholder="输入消息..."
              @keyup.enter="sendMessage"
              class="message-input"
            />
            <button 
              @click="sendMessage" 
              :disabled="!newMessage.trim()" 
              class="send-btn"
            >
              <span class="send-icon">📤</span>
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

export default {
  name: 'ChatRoom',
  setup() {
    const username = ref('')
    const newMessage = ref('')
    const messages = ref([])
    const connected = ref(false)
    const socket = ref(null)
    const messagesContainer = ref(null)
    const onlineUsers = ref(1)
    const userList = ref([])

    const connect = () => {
      if (!username.value.trim()) {
        alert('请输入用户名')
        return
      }

      try {
        socket.value = new WebSocket('ws://localhost:80/api/socket/chat')
        
        socket.value.onopen = () => {
          connected.value = true
          console.log('WebSocket 连接已建立')
          
          // 模拟添加当前用户到用户列表
          userList.value.push({ id: Date.now(), name: username.value })
          
          // 发送加入消息
          const joinMessage = {
            type: 'JOIN',
            sender: username.value,
            content: `${username.value} 加入了聊天室`,
            room: 'public'
          }
          socket.value.send(JSON.stringify(joinMessage))
        }

        socket.value.onmessage = (event) => {
          const message = JSON.parse(event.data)
          messages.value.push(message)
          scrollToBottom()
          
          // 模拟用户加入/离开时更新用户列表
          if (message.type === 'JOIN' && message.sender !== username.value) {
            userList.value.push({ id: Date.now(), name: message.sender })
            onlineUsers.value = userList.value.length
          } else if (message.type === 'LEAVE') {
            userList.value = userList.value.filter(user => user.name !== message.sender)
            onlineUsers.value = userList.value.length
          }
        }

        socket.value.onclose = () => {
          connected.value = false
          console.log('WebSocket 连接已关闭')
          userList.value = []
          onlineUsers.value = 0
        }

        socket.value.onerror = (error) => {
          console.error('WebSocket 错误:', error)
          alert('连接失败，请检查服务器是否运行')
        }

      } catch (error) {
        console.error('创建 WebSocket 连接失败:', error)
        alert('创建连接失败')
      }
    }

    const sendMessage = () => {
      if (newMessage.value.trim() && socket.value && socket.value.readyState === WebSocket.OPEN) {
        const chatMessage = {
          type: 'CHAT',
          sender: username.value,
          content: newMessage.value.trim(),
          room: 'public'
        }

        socket.value.send(JSON.stringify(chatMessage))
        newMessage.value = ''
      }
    }

    const disconnect = () => {
      if (socket.value) {
        // 发送离开消息
        const leaveMessage = {
          type: 'LEAVE',
          sender: username.value,
          content: `${username.value} 离开了聊天室`,
          room: 'public'
        }
        socket.value.send(JSON.stringify(leaveMessage))
        
        // 短暂延迟后关闭连接
        setTimeout(() => {
          socket.value.close()
        }, 100)
      }
      connected.value = false
      messages.value = []
      username.value = ''
      userList.value = []
      onlineUsers.value = 0
    }

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }

    const formatTime = (timestamp) => {
      if (!timestamp) {
        // 如果没有时间戳，使用当前时间
        const now = new Date()
        return now.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
      
      // 如果时间戳是字符串，尝试解析
      let date
      if (typeof timestamp === 'string') {
        date = new Date(timestamp)
      } else {
        date = new Date(timestamp)
      }
      
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }

    onUnmounted(() => {
      disconnect()
    })

    return {
      username,
      newMessage,
      messages,
      connected,
      messagesContainer,
      onlineUsers,
      userList,
      connect,
      sendMessage,
      disconnect,
      formatTime
    }
  }
}
</script>

<style scoped>
/* 全屏布局 */
.fullscreen-chat {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 状态栏 */
.chat-status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.app-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2c3e50;
}

.user-info {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  padding: 5px 10px;
  border-radius: 20px;
}

.connected {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.1);
}

.disconnected {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.connected .dot {
  background: #27ae60;
  animation: pulse 2s infinite;
}

.disconnected .dot {
  background: #e74c3c;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.disconnect-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s;
}

.disconnect-btn:hover {
  background: #c0392b;
}

/* 全屏登录界面 */
.fullscreen-login {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  text-align: center;
  max-width: 450px;
  width: 100%;
}

.login-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.login-subtitle {
  color: #7f8c8d;
  margin-bottom: 30px;
  font-size: 1.1rem;
}

.login-form {
  margin-bottom: 30px;
}

.input-group {
  margin-bottom: 20px;
  text-align: left;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.login-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.login-input:focus {
  outline: none;
  border-color: #3498db;
}

.login-btn {
  width: 100%;
  padding: 15px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover {
  background: #2980b9;
}

.login-features {
  display: flex;
  justify-content: space-around;
  margin-top: 30px;
}

.feature {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.feature-icon {
  font-size: 1.5rem;
}

/* 全屏聊天室 */
.fullscreen-chat-room {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏 */
.chat-sidebar {
  width: 250px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 15px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.sidebar-header h3 {
  margin: 0;
  color: #2c3e50;
}

.user-list {
  flex: 1;
  padding: 10px 0;
  overflow-y: auto;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  transition: background 0.2s;
}

.user-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.user-name {
  color: #2c3e50;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
}

/* 消息容器 */
.messages-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 消息样式 */
.system-message {
  text-align: center;
  color: #7f8c8d;
  font-size: 0.9rem;
  padding: 8px 0;
}

.system-icon {
  margin-right: 5px;
}

.message-time {
  margin-left: 10px;
  font-size: 0.8rem;
  color: #95a5a6;
}

.user-message {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.own-message {
  align-self: flex-end;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 0.85rem;
}

.own-header {
  justify-content: flex-end;
}

.message-sender {
  font-weight: bold;
  color: #3498db;
}

.message-content {
  padding: 12px 15px;
  border-radius: 18px;
  background: #e0e0e0;
  color: #333;
  line-height: 1.4;
  word-break: break-word;
}

.own-content {
  background: #3498db;
  color: white;
  border-bottom-right-radius: 5px;
}

.user-message:not(.own-message) .message-content {
  border-bottom-left-radius: 5px;
}

/* 输入区域 */
.input-container {
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.input-wrapper {
  display: flex;
  gap: 10px;
  max-width: 100%;
}

.message-input {
  flex: 1;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.message-input:focus {
  outline: none;
  border-color: #3498db;
}

.send-btn {
  padding: 0 25px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.send-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.send-btn:not(:disabled):hover {
  background: #2980b9;
}

.send-icon {
  font-size: 1.1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-sidebar {
    display: none;
  }
  
  .user-message {
    max-width: 85%;
  }
  
  .status-info {
    flex-direction: column;
    gap: 5px;
    align-items: flex-start;
  }
  
  .connection-status {
    flex-direction: column;
    gap: 10px;
    align-items: flex-end;
  }
  
  .login-container {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 2rem;
  }
}
</style>