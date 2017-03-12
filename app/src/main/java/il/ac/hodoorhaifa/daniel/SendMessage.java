package il.ac.hodoorhaifa.daniel;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import il.ac.hodoorhaifa.daniel.Interfaces.IReceiveResponse;

public class SendMessage extends AsyncTask<il.ac.hodoorhaifa.daniel.Contracts.Message.Message,String,Boolean> {
    private Exception exception;
    public List<? extends IReceiveResponse> listeners;

    public SendMessage(){
        listeners = new ArrayList<IReceiveResponse>();
    }

    @Override
    protected Boolean doInBackground(il.ac.hodoorhaifa.daniel.Contracts.Message.Message... messages) {
        StringBuilder responseString = new StringBuilder();
        try{
            Socket socket = new Socket();
            socket.setSoTimeout(1000);
            socket.connect(new InetSocketAddress("132.75.55.155", 7353), 1000);
            PrintWriter outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            Gson gson = new Gson();
            String json = gson.toJson(messages[0]);
            outToServer.print(json);
            outToServer.flush();
            if (messages[0].getIsLogin().equals(false)){
                publishProgress("Door is Open", "true");
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                responseString.append(str);
            }
            socket.close();

        }catch(Exception e){
            this.exception = e;
            e.printStackTrace();
            publishProgress("Connection Error!", "true");
            return false;
        }
        return (responseString.toString().startsWith("Success"));
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        for (IReceiveResponse listener : listeners) {
            listener.changeErrorMessage(values[0], values[1].equals("true"));
        }
    }

    @Override
    protected void onPostExecute(Boolean allGood){
        for (IReceiveResponse listener : listeners)
            listener.serverSentResponse(allGood);
    }
}