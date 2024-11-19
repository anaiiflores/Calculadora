package com.example.calculadoraa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private TextView memoryDisplay;
    private StringBuilder expresionActual = new StringBuilder();
    private double memoria = 0.0;
    private boolean esResultadoMostrado = false;
    private List<Double> listaMemoria = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        memoryDisplay = findViewById(R.id.memorytotal);

        configurarListeners();
    }

    private void configurarListeners() {
        int[] botonesIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9, R.id.buttonSuma, R.id.buttonResta,
                R.id.buttonMultiply, R.id.buttonDivide, R.id.buttonEquals,
                R.id.buttonB1, R.id.buttonMr, R.id.buttonB3, R.id.buttonMMenos,
                R.id.buttonMs, R.id.buttonMTotal
        };




        for (int id : botonesIds) {
            Button boton = findViewById(id);
            boton.setOnClickListener(this::alPresionarBoton);
        }
    }

    private void alPresionarBoton(View vista) {
        Button boton = (Button) vista;
        String textoBoton = boton.getText().toString();

        if (esResultadoMostrado && !"=".equals(textoBoton) && !textoBoton.matches("[MC|MR|M+|M-|MS|MTotal]")) {
            expresionActual.setLength(0);
            esResultadoMostrado = false;
        }

        switch (textoBoton) {



            case "=":
                calcularResultado();
                break;
            case "+":
            case "-":
            case "*":
            case "/":
                agregarOperador(textoBoton);
                break;
            case "MC":
                limpiarMemoria();
                break;
            case "MR":
                recordarMemoria();
                break;
            case "M+":
                sumarMemoria();
                break;
            case "M-":
                restarMemoria();
                break;
            case "MS":
                guardarMemoria();
                break;
            case "MTotal":
                mostrarMemoriaTotal();
                break;
            default:
                expresionActual.append(textoBoton);
                display.setText(expresionActual.toString());
                break;
        }
    }

    private void agregarOperador(String operador) {
        if (expresionActual.length() > 0) {
            char ultimoCaracter = expresionActual.charAt(expresionActual.length() - 1);
            if ("+-*/".indexOf(ultimoCaracter) != -1) {
                expresionActual.setCharAt(expresionActual.length() - 1, operador.charAt(0));
            } else {
                expresionActual.append(operador);
            }
            display.setText(expresionActual.toString());
            esResultadoMostrado = false;
        }
    }

    private void calcularResultado() {
        try {
            double resultado = evaluarExpresion(expresionActual.toString());
            display.setText(String.valueOf(resultado));
            expresionActual.setLength(0);
            expresionActual.append(resultado);
            esResultadoMostrado = true;
        } catch (Exception e) {
            display.setText("Error");
            expresionActual.setLength(0);
        }
    }

    // Funciones de memoria
    private void limpiarMemoria() {
        memoria = 0.0;
        listaMemoria.clear(); // Limpiar la lista de memoria también
        memoryDisplay.setText("0.0");
    }

    private void recordarMemoria() {
        expresionActual.setLength(0);
        expresionActual.append(memoria);
        display.setText(expresionActual.toString());
        esResultadoMostrado = false;
    }

    private void sumarMemoria() {
        double resultado = evaluarExpresionActual();
        memoria += resultado;
        listaMemoria.add(resultado); // Añadir el valor a la lista
    }

    private void restarMemoria() {
        double resultado = evaluarExpresionActual();
        memoria -= resultado;
        listaMemoria.add(-resultado); // Añadir el valor negativo a la lista
    }

    // Función modificada de MS para solo añadir números simples a la memoria
    private void guardarMemoria() {
        // Verificamos si la expresión no contiene operadores, es decir, es solo un número
        String expresion = expresionActual.toString();
        if (!expresion.isEmpty() && !expresion.matches(".*[+\\-*/].*")) { // Verifica que no haya operadores
            try {
                double resultado = Double.parseDouble(expresion);
                memoria = resultado;
                listaMemoria.add(resultado); // Almacenar en la lista
                memoryDisplay.setText("Set: " + resultado); // Mostrar en pantalla
            } catch (NumberFormatException e) {
                display.setText("Error");
            }
        } else {
            display.setText("Error: Ingrese un número válido");
        }
    }

    private void mostrarMemoriaTotal() {
        StringBuilder memoriaText = new StringBuilder();
        for (Double valor : listaMemoria) {
            memoriaText.append(valor).append("\n"); // Cada valor en una nueva línea
        }
        memoryDisplay.setText(memoriaText.toString()); // Mostrar todos los valores de memoria
    }

    private double evaluarExpresionActual() {
        try {
            return evaluarExpresion(expresionActual.toString());
        } catch (Exception e) {
            display.setText("Error");
            expresionActual.setLength(0);
            return 0.0;
        }
    }

    private double evaluarExpresion(String expresion) throws Exception {
        expresion = expresion.replaceAll("--", "+");

        String[] tokens = expresion.split("(?<=[-+*/])|(?=[-+*/])");
        double resultado = Double.parseDouble(tokens[0]);

        for (int i = 1; i < tokens.length; i += 2) {
            String operador = tokens[i];
            double siguienteNumero = Double.parseDouble(tokens[i + 1]);

            switch (operador) {
                case "+":
                    resultado += siguienteNumero;
                    break;
                case "-":
                    resultado -= siguienteNumero;
                    break;
                case "*":
                    resultado *= siguienteNumero;
                    break;
                case "/":
                    if (siguienteNumero == 0) {
                        throw new ArithmeticException("División por cero");
                    }
                    resultado /= siguienteNumero;
                    break;
                default:
                    throw new IllegalArgumentException("Operador inválido");
            }
        }

        return resultado;
    }
}
