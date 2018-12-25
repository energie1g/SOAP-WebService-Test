package world.soapwebservice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import android.os.Handler;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SoapTestActivity extends Activity {
    TextView result;

    // use handler to keep GUI update on behalf of background tasks
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
            super.handleMessage(msg);
            String text = (String) msg.obj;
            result.append("\n" + text);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap_test);
        result = (TextView) findViewById(R.id.result);
        //do slow calls to remote server in a background thread
        Thread slowJob = new Thread() {
            @Override
            public void run() {
                // IP address at home
                final String URL = "http://192.168.1.144/SOAP_WS/Service1.asmx";
                final String NAMESPACE = "http://MyHostNameOrIPAddress/";
                final String METHOD_NAME = "getPersonList";
                String resultValue = "";

                try {
                    // prepare SOAP REQUEST (namespace, method, arguments)
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                    Log.d("SOAP-G-SHIT", "Request: -> " + request);

                    // passing primitive (simple) input parameters
                    request.addProperty("home", "Winterfell Castle");
                    Log.d("SOAP-G-SHIT", "Passing Input: -> " + request);

                    // prepare ENVELOPE put request inside
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    Log.d("SOAP-G-GHIT", "ENVOLOPE: -> " + envelope);

                    // tell the type of complex object to be returned by service
                    envelope.addMapping(NAMESPACE, METHOD_NAME, new ArrayList<Person>().getClass());
                    Log.d("SOAP-G-SHIT", "TELL THE TYPE TO BE RETURNED: -> " + envelope);

                    // TRANSPORT envelope to destination set by URL (call & wait)
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                    androidHttpTransport.call(NAMESPACE + METHOD_NAME, envelope);
                    Log.d("SOAP-G-SHIT", "TRANSPORT THE ENVELOPE: -> " + androidHttpTransport);

                    // receiving a complex response object (list of Person objects)
                    SoapObject response = (SoapObject) envelope.getResponse();
                    Log.d("SOAP-G-SHIT", "RESPONSE: -> " + response);

                    if (response == null) {
                        resultValue = "NULL response received";
                    } else {

                    // get ready to show rows arriving from the server
                        resultValue = "RESPONSE\n" + response.toString();
                        resultValue += "\n\nPERSON-LIST";

                    // use KSOAP access methods to parse and extract data from response
                        for (int i = 0; i < response.getPropertyCount(); i++) {
                            resultValue += "\nRow-" + i;
                            resultValue += "\n\tKSOAP\n\t" + response.getProperty(i);
                            SoapObject personObj = (SoapObject)response.getProperty(i);
                            Person p = new Person(personObj);
                            resultValue += "\n\tJAVA:\n\t" + p.toString();
                        }
                    }
                } catch (Exception e) {
                    resultValue = "\nERROR: " + e.getMessage();
                }
                // send message to handler so it updates GUI
                Message msg = handler.obtainMessage();
                msg.obj = (String) resultValue;
                handler.sendMessage(msg);
            }
        };
        slowJob.start();
    }// onCreate
}
