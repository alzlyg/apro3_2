package innobi.ru.ap3asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Делатель запросов (класс, умеющий запрашивать страницы)
public class RequestMaker {

    // Слушатель, при помощи которого отправим обратный вызов о готовности страницы
    private OnRequestListener listener;

    // В конструкторе примем слушателя, а в дальнейшем передадим его асинхронной задаче
    public RequestMaker(OnRequestListener onRequestListener) {
        listener = onRequestListener;
    }

    // Сделать запрос
    public void make() {
        // Создаем объект асинхронной задачи (передаем ей слушатель)
        Requester requester = new Requester(listener);
        // Запускаем асинхронную задачу
        requester.execute();
    }

    // Интерфейс слушателя с методами обратного вызова
    public interface OnRequestListener {
        void onStatusProgress(String updateProgress);   // Вызов для обновления прогресса

        void onComplete(String result);                 // Вызов при завершении обработки
    }

    // AsyncTask - это обертка для выполнения потока в фоне
    // Начальные и конечные методы работают в потоке UI, а основной метод расчета работает в фоне
    private static class Requester extends AsyncTask<String, String, String> {
        private OnRequestListener listener;

        Requester(OnRequestListener listener) {
            this.listener = listener;
        }

        private int calculate() {
            double r = 1;
            for (int i = 1; i <= 100; i++) {
                for (int j = 0; j < 10000000; j++) {
                    r = j * 0.01 + r / 0.01;
                }
                publishProgress(String.format("Вычисления %d %%", i));
            }
            return 42;
        }

        // Обновление прогресса, работает в основном потоке UI
        @Override
        protected void onProgressUpdate(String... strings) {
            listener.onStatusProgress(strings[0]);
        }

        // Выполнить таск в фоновом потоке
        @Override
        protected String doInBackground(String... strings) {
            return getResourceUri(/*strings[0]*/);
        }

        // Выдать результат (работает в основном потоке UI)
        @Override
        protected void onPostExecute(String content) {
            listener.onComplete(content);
        }

        // Обработка загрузки страницы
        private String getResourceUri() {
            calculate();
            return "Завершено";
        }
    }
}