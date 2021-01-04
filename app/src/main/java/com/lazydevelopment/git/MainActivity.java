package com.lazydevelopment.git;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText edtPaste; // Поле ввода текста
    Button btnPaste, btnEnter, btnReset; // Кнопки "Вставить URL" и "Перейти"
    WebView webView; // Вебвью
    String text; // Текст URL
    private static long back_pressed;// Время нажатия кнопки назад
    boolean first_start = false; // Логическая переменная первого включения
    ClipboardManager clipboardManager; // Клипборд мэнэджер для работы с буфером обмена
    SharedPreferences sPref; // Сохранение данных приложения
    final String SAVED_TEXT = "url_text"; // Часть системы сохранения данных
    final String FIRST = "first_start"; // Часть системы сохранения данных


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Инициализация и настройка вебвью
        webView = findViewById(R.id.main);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // Использование вебвью клиента для открытия всех переходов внутри приложения
        webView.setWebViewClient(new WebViewClient());
        // Инициализация всех элементов
        edtPaste=(EditText)findViewById(R.id.urltext); // Поле текста
        btnPaste=(Button)findViewById(R.id.paste); // Кнопка вставить
        btnEnter=(Button)findViewById(R.id.enter); // Кнопка перейти
        clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE); // Работа с буфером обмена
        // Звгрузка существующего сохранения в приложении или установка настроек по умолчанию
        loadURL();
        // Работа кнопки вставки
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData data = clipboardManager.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0);

                text = item.getText().toString();
                edtPaste.setText(text);
                Toast.makeText(getApplicationContext(),"URL Вставлено ",Toast.LENGTH_SHORT).show();
            }
        });
        // Работа кнопки перехода по ссылке
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(text);
                Toast.makeText(getApplicationContext(),"Загрузка страницы",Toast.LENGTH_SHORT).show();
                saveURL();
            }
        });
    }
    // Обработка нажатия кнопки назад
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        if (back_pressed + 1000 > System.currentTimeMillis()){
            webView.setVisibility(View.INVISIBLE);
            sPref = getSharedPreferences("sPref", MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean(FIRST , false);
            ed.putString(SAVED_TEXT, "");
            ed.commit();
        }
        else
            Toast.makeText(getBaseContext(), "Нажмите еще раз для сброса URL",
                    Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
    // Сохранение данных приложения
    private void saveURL() {
        sPref = getSharedPreferences("sPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(FIRST , true);
        ed.putString(SAVED_TEXT, text);
        ed.commit();
    }
    // Загрузка данных приложения
    private void loadURL() {
        sPref = getSharedPreferences("sPref", MODE_PRIVATE);
        text = sPref.getString(SAVED_TEXT, "");
        first_start = sPref.getBoolean(FIRST, false);
        if (first_start) { webView.setVisibility(View.VISIBLE);
            webView.loadUrl(text);}
    }
    // Сохранение данных при закрытии
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveURL();
    }
}