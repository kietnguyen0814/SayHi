package com.example.kietnguyen.sayhi;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    //Khai báo
    private TextToSpeech txtSpeech;
    private ImageButton btnListen1;  //button Listen 1
    private ImageButton btnListen2;  //button Listen 2
    private ImageButton btnNext;  //button Next
    private ImageButton btnPre;  //button Pre
    private ImageButton btnSpeech;
    private ImageButton btnReSpeech;

    private TextView lblWrongWord;
    private TextView lblRightWord;
    private TextView lblUserSpeechAgain;

    private TextView txtComputer;
    private TextView txtUserSpeech;
    private TextView txtUserSpeechAgain;
    private TextView txtPoint;

    private static int lstIndex=1;
    private static boolean isIncrease=true;
    Spinner dropdownTrue,dropdownFalse;
    private static String[] lstItems = new String[]{"I won't take but a minute","you're so pretty", "you are very beautiful", "how's it going","you'll have to step on it"
            ,"cut your coat according to your cloth","birds of a feather flock together","where there's life There's Hope",
            "a picture is worth a thousand words","you scratch my back and I'll scratch yours"};
    private static String strTrueWord;
    private final int SPEECH_RECOGNITION_CODE_USERSPEECH = 1; // use for speech to text
    private final int SPEECH_RECOGNITION_CODE_USERSPEECHAGAIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeech = new TextToSpeech(this, (TextToSpeech.OnInitListener) this);

        btnListen1 = (ImageButton) findViewById(R.id.btnListen1);
        btnListen2 = (ImageButton) findViewById(R.id.btnListen2);
        btnSpeech=(ImageButton) findViewById(R.id.btnSpeech);
        btnReSpeech=(ImageButton) findViewById(R.id.btnReSpeech);
        btnNext=(ImageButton) findViewById(R.id.btnNext);
        btnPre=(ImageButton) findViewById(R.id.btnPrevious);

        dropdownTrue = (Spinner)findViewById(R.id.spinRight);
        dropdownFalse = (Spinner)findViewById(R.id.spinnerWrong);

        txtComputer = (TextView) findViewById(R.id.txtComputer);
        txtUserSpeech = (TextView) findViewById(R.id.txtUserSpeech);
        txtUserSpeechAgain = (TextView) findViewById(R.id.txtUserSpeechAgain);

        lblWrongWord =(TextView)findViewById(R.id.lblWrongWord);
        lblRightWord =(TextView)findViewById(R.id.lblRightWord);
        lblUserSpeechAgain =(TextView)findViewById(R.id.lblUserSpeechAgain);

        txtPoint = (TextView) findViewById(R.id.txtPoint);//String a = txtComputer.gw
        txtComputer.setText(lstItems[0]);
        // Bắt sự kiện click button
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                hideDetail();
                startSpeechToText(SPEECH_RECOGNITION_CODE_USERSPEECH);

                //sendMessage(findViewById(android.R.id.content));
            }
        });

        btnReSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText(SPEECH_RECOGNITION_CODE_USERSPEECHAGAIN);
            }
        });

        btnListen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(txtComputer.getText().toString());
            }
        });
        btnListen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(dropdownTrue.getSelectedItem().toString());
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNext(findViewById(android.R.id.content));
                hideDetail();
                txtUserSpeech.setText("");
                txtPoint.setText("");
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPre(findViewById(android.R.id.content));
                hideDetail();
                txtUserSpeech.setText("");
                txtPoint.setText("");
            }
        });
        /*btnListen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        dropdownTrue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectSpinnerItemByValue(dropdownFalse,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        dropdownFalse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectSpinnerItemByValue(dropdownTrue,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    //Hàm hủy
    @Override
    public void onDestroy() {

        if (txtSpeech != null) {
            txtSpeech.stop();
            txtSpeech.shutdown();
        }
        super.onDestroy();
    }

    //MARK : - Speech To Text
    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, code);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE_USERSPEECH: {
                // Split String in here
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtUserSpeech.setText(text);
                    sendMessage(findViewById(android.R.id.content));
                    //checkString(txtComputer,txtUserSpeech);
                }
                break;
            }
            case SPEECH_RECOGNITION_CODE_USERSPEECHAGAIN: {
                // Split String in here
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtUserSpeechAgain.setText(text);
                    checkString(dropdownTrue,txtUserSpeechAgain);
                }
                break;
            }
        }
    }


    //Text To Speech
    @Override
    public void onInit(int status) {
        //nếu khởi tạo thành công
        if (status == TextToSpeech.SUCCESS) {

            int result = txtSpeech.setLanguage(Locale.ENGLISH);  //Cài đặt cho ngôn ngữ cần phát âm

            if (result == TextToSpeech.LANG_MISSING_DATA  // dữ liệu ngôn ngữ bị thiếu
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) //ngôn ngữ không được hỗ trợ
            {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnListen1.setEnabled(true);  //button hiện ra
            }

        } else { //Khởi tạo thất bại
            Log.e("TTS", "Initilization Failed!");
        }

    }

    //Hàm thực hiện việc phát âm
    private void speakOut(String strCha) {

        //if(temp > 0) {  //Kiểm tra ngăn phát âm khi vừa run app
        CharSequence text = strCha;
        txtSpeech.setSpeechRate((float) 0.95); //tốc độ phát âm
        txtSpeech.setPitch((float) 1.25); //tạo độ cao của giọng phát âm
        txtSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1"); //thực hiện phát âm
        // }

        // temp ++;
    }

    public void sendMessage(View view)
    {
        try{
            double countTrue=0;
            String str1=txtComputer.getText().toString();
            String str2=txtUserSpeech.getText().toString();


            String[] Strings=str1.split(" ");
            String[] Strings2=str2.split(" ");

            String strTempTrue = "";
            String strTempFalse = "";

            String strTrue = "";
            String strFalse = "";

            String[] lstTrue;
            String[] lstFalse;

            int index = 0;
            boolean flag = false;

            if(Strings.length<Strings2.length)
            {
                txtUserSpeech.setTextColor(Color.RED);
                hideDetail();
                Log.d("case result","1");
            }
            else if(Strings.length==Strings2.length)
            {
                if (str1.equals(str2))
                {
                    txtUserSpeech.setTextColor(Color.GREEN);
                    hideDetail();
                }
                for(int i=0;i<Strings.length;i++)
                {
                    if(!Strings[i].equals(Strings2[i]))
                    {
                        txtUserSpeech.setTextColor(Color.RED);
                        showDetail();
                        if(strTrue.length()==0)
                        {
                            strTrue+=Strings[i];
                        }
                        else
                        {
                            strTrue+=","+Strings[i];
                        }
                        if(strFalse.length()==0)
                        {
                            strFalse+=Strings2[i];
                        }
                        else
                        {
                            strFalse+=","+Strings2[i];
                        }
                    }
                    else {
                        countTrue++;
                    }
                }
            }
            else
            {
                txtUserSpeech.setTextColor(Color.RED);
                for (int i = 0; i < Strings2.length;i++ )
                {
                    if (i == Strings2.length - 1 && index < Strings.length - 1 && Strings2[i].equals(Strings[index]))
                    {
                        if (strFalse.length() == 0)
                        {
                            strFalse += "";
                        }
                        else
                        {
                            strFalse += ",";
                        }
                        if (strTrue.length() == 0)
                        {
                            strTrue += "";
                        }
                        else
                        {
                            strTrue += ",";
                        }
                        for(int h=index+1;h<Strings.length;h++)
                        {
                            if (strTrue.length() == 0)
                            {
                                strTrue += Strings[h];
                            }
                            else
                            {
                                strTrue += " " + Strings[h];
                            }
                        }
                    }
                    else
                    {
                        if (index == Strings.length - 1 && i < Strings2.length - 1 && Strings2[i].equals(Strings[index]))
                        {
                            if (strFalse.length() == 0)
                            {
                                strFalse += "";
                            }
                            else
                            {
                                strFalse += ",";
                            }
                            if (strTrue.length() == 0)
                            {
                                strTrue += "";
                            }
                            else
                            {
                                strTrue += ",";
                            }
                            for (int h = i+1; h < Strings2.length; h++)
                            {
                                if (strFalse.length() == 0)
                                {
                                    strFalse += Strings2[h];
                                }
                                else
                                {
                                    strFalse += " " + Strings2[h];
                                }
                            }
                        }
                        else
                        {
                            if (i == Strings2.length - 1 && index < Strings.length - 1 && !Strings2[i].equals(Strings[index]))
                            {
                                if (strFalse.length() == 0)
                                {
                                    strFalse += Strings2[i];
                                }
                                else
                                {
                                    strFalse += "," + Strings2[i];
                                }
                                if (strTrue.length() > 0)
                                {
                                    strTrue += ",";
                                }
                                for (int h = index; h < Strings.length; h++)
                                {
                                    if (strTrue.length() == 0)
                                    {
                                        strTrue += Strings[h];
                                    }
                                    else
                                    {
                                        strTrue += " " + Strings[h];
                                    }
                                }
                            }
                            else
                            {
                                if (!Strings2[i].equals( Strings[index]))
                                {
                                    flag = false;
                                    String strSTrue = Strings[index];
                                    String strSFalse = Strings2[i];
                                    for (int j = i + 1; j < Strings2.length; j++)
                                    {
                                        strTempTrue = "";
                                        for (int h = index + 1; h < Strings.length; h++)
                                        {
                                            if (Strings2[j].equals(Strings[h]))
                                            {
                                                if (strTrue.length() == 0)
                                                {
                                                    strTrue += strSTrue + strTempTrue;
                                                }
                                                else
                                                {
                                                    strTrue += "," + strSTrue + " " + strTempTrue;
                                                }
                                                if (strFalse.length() == 0)
                                                {
                                                    strFalse += strSFalse + strTempFalse;
                                                }
                                                else
                                                {
                                                    strFalse += "," + strSFalse + " " + strTempFalse;
                                                }
                                                flag = true;
                                                index = h;
                                                break;
                                            }
                                            else
                                            {
                                                strTempTrue += " " + Strings[h];
                                            }
                                        }
                                        if (flag)
                                        {
                                            strTempFalse = "";
                                            i = j - 1;
                                            break;
                                        }
                                        else
                                        {
                                            strTempFalse += " " + Strings2[j];
                                        }
                                    }
                                    if(flag==false)
                                    {
                                        for (int u = index+1; u < Strings.length;u++ )
                                        {
                                            strSTrue += " " + Strings[u];
                                        }
                                        index = Strings.length;
                                        for (int u = i+1; u < Strings2.length; u++)
                                        {
                                            strSFalse += " " + Strings2[u];
                                        }
                                        i = Strings2.length;
                                        if (strTrue.length() == 0)
                                        {
                                            strTrue += strSTrue;
                                        }
                                        else
                                        {
                                            strTrue += "," + strSTrue;
                                        }
                                        if (strFalse.length() == 0)
                                        {
                                            strFalse += strSFalse;
                                        }
                                        else
                                        {
                                            strFalse += "," + strSFalse;
                                        }
                                    }
                                }
                                else
                                {
                                    index++;
                                    countTrue++;
                                }
                            }
                        }
                    }
                }
                showDetail();
            }
            double userPoint=(countTrue/Strings.length)*100;
            txtPoint.setText(""+userPoint);
            lstTrue = strTrue.split(",");
            lstFalse = strFalse.split(",");
            ArrayAdapter<String> adapterTrue = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstTrue);
            dropdownTrue.setAdapter(adapterTrue);
            ArrayAdapter<String> adapterFalse = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lstFalse);
            dropdownFalse.setAdapter(adapterFalse);
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void getNext(View view)
    {
        if(lstIndex<lstItems.length) {
            if(isIncrease==false)
            {
                isIncrease=true;
                lstIndex++;
            }
            txtComputer.setText(lstItems[lstIndex]);
            lstIndex++;

        }
    }
    private void getPre(View view)
    {
        if(lstIndex>0) {
            if(isIncrease==true)
            {
                isIncrease=false;
                lstIndex--;
            }
            lstIndex--;
            txtComputer.setText(lstItems[lstIndex]);
        }
    }
    public static void selectSpinnerItemByValue(Spinner spnr, long value) {
        ArrayAdapter adapter = (ArrayAdapter) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItemId(position) == value) {
                spnr.setSelection(position);
                //strTrueWord = spnr.getSelectedItem().toString();
                return;
            }
        }
    }

    public void showDetail(){
        lblWrongWord.setVisibility(View.VISIBLE);
        lblRightWord.setVisibility(View.VISIBLE);
        lblUserSpeechAgain.setVisibility(View.VISIBLE);
        dropdownTrue.setVisibility(View.VISIBLE);
        dropdownFalse.setVisibility(View.VISIBLE);
        btnReSpeech.setVisibility(View.VISIBLE);
        btnListen2.setVisibility(View.VISIBLE);
        txtUserSpeechAgain.setVisibility(View.VISIBLE);
    }
    public void hideDetail(){
        lblWrongWord.setVisibility(View.INVISIBLE);
        lblRightWord.setVisibility(View.INVISIBLE);
        lblUserSpeechAgain.setVisibility(View.INVISIBLE);
        dropdownTrue.setVisibility(View.INVISIBLE);
        dropdownFalse.setVisibility(View.INVISIBLE);
        btnReSpeech.setVisibility(View.INVISIBLE);
        btnListen2.setVisibility(View.INVISIBLE);
        txtUserSpeechAgain.setVisibility(View.INVISIBLE);
    }

    public void checkString(Spinner spinA, TextView txtB){
        String stringA = spinA.getSelectedItem().toString();
        String stringB = txtB.getText().toString();
        if (stringA.equals(stringB)){
            txtB.setTextColor(Color.GREEN);
        } else {
            txtB.setTextColor(Color.RED);
        }
    }

}
