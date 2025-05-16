Set ws = CreateObject("WScript.Shell")
' 切换到指定目录
ws.CurrentDirectory = "D:\home\znsx\net-proxy"
' 执行命令（窗口可见）
ws.Run ".\jre\bin\java.exe -jar app.jar --spring.config.location=conf.properties", 0, False