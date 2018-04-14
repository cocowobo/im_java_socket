import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;

public class IM_Client{
	
	private Socket client;
    private PrintWriter oos;
    private InputStream ois;
    private ListenThread listenThread = new ListenThread();
    private static int client_id = 1;
    private static final String SERVER_HOST = "localhost";
    private static final int LISTEN_PORT = 6788;

    private class ListenThread extends Thread {
        @Override
        public void run() {
			
            while (true) {
                try {
					byte[] buf = new byte[2048];   
					int i = ois.read(buf);  
					String message = new String(buf,0,i);
					//System.out.println("�յ���Ϣ��"+message);
                    if(message.equals("���ӳɹ�")){
                       new Thread(new Runnable(){
						   public void run(){
							   toInputMessage();
						   }
					   }).start();
                    }else{
						System.out.println(message);
						//toInputMessage();
					}
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	public void toInputMessage(){
		Scanner sc = new Scanner(System.in);
        while (true){
			System.out.println("����������ִ���:");
			oos.write(sc.nextLine());
			//oos.flush();
			oos.write("#");
			System.out.println("���뷢�����ݣ�");
			oos.write(sc.nextLine());
			oos.flush();
         }
	}

    public void connect() {
        try {
            client = new Socket(SERVER_HOST, LISTEN_PORT);
            ois = client.getInputStream();
            oos = new PrintWriter(client.getOutputStream());
            oos.write(String.valueOf(client_id++)); // �Ա�����
            oos.flush();
            // �������Ӻ�ʼ������Ϣ
            listenThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new IM_Client().connect();
    }
}