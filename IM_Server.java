import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;


public class IM_Server{
	
    private static final int LISTEN_PORT = 6788;
    private static final int DEFAULT_BACKLOG = 100;
    private ServerSocket server;
    private ExecutorService execs;
    private Map<String, User> userList;

    public IM_Server() {
        try {
            server = new ServerSocket(LISTEN_PORT, DEFAULT_BACKLOG);
            execs = Executors.newCachedThreadPool();
            userList = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        while (true) {
            try {
                Socket client = server.accept();    // 该方法会阻塞
                final User user = new User(client);
                System.out.println("用户" + user.getName() + "加入");
                userList.put(user.getName(), user); // 用户加入服务器
				user.getOutput().write("连接成功");
				user.getOutput().flush();
                execs.execute(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
								String message = input2String(user.getInput());
								System.out.println("服务器中转："+message);
                                String toUser = message.substring(0,message.indexOf("#"));
                                final String msg = message.substring(message.indexOf("#")+1,message.length());

                                // 在服务器中搜寻该用户  
                                for (Map.Entry<String, User> entry : userList.entrySet()) {
                                    if (entry.getKey().equals(toUser)) {
                                        final User target = entry.getValue();
										//System.out.println("我是："+user.getName()+",目标好友："+target.getName());
										target.getOutput().write(user.getName() + " 对你说:" + msg);
										target.getOutput().flush();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new IM_Server().startServer();
    }

    class User {

        public User(Socket client) throws IOException, ClassNotFoundException {
			if(client!=null){
				input = client.getInputStream();
				output = new PrintWriter(client.getOutputStream(),true); 
			}
            if (input != null)
                name = input2String(input);
        }

        String name;
        InputStream input;
        PrintWriter output;

        public String getName() {
            return name;
        }

        public InputStream getInput() {
            return input;
        }

        public PrintWriter getOutput() {
            return output;
        }
    }

    public String input2String(InputStream input) throws IOException{
		byte[] buf = new byte[2048];   
		int i = input.read(buf);  
		String request = new String(buf,0,i);
		return request;
	}
}