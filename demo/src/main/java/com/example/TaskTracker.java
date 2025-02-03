package com.example;

import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;


public class TaskTracker {

    private static final String FILE_PATH = "demo/data/task.json";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Ingrese un comando(add, update, delete, list, exit)");
            String command = scanner.nextLine();

            switch (command) {
                case "add":
                    addTask(scanner);
                    break;
                case "update":
                    updateTask(scanner);
                    break;
                case "delete":                
                    deleteTask(scanner);
                    break;
                case "list":
                    listTasks();
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.out.println("Comando no reconocido, Intente de Nuevo");
                    break;
            }
        }
        scanner.close();
        System.out.println("¡Adiós!");
    }

    // ----------------------------METODO AddTask--------------------------//

    private static void addTask(Scanner scanner) {
        System.out.println("Ingrese la descripción de la tarea:");
        String description = scanner.nextLine();
        String createdAt = LocalDateTime.now().toString();
        JSONArray tasks = loadTasks();
        int newId = tasks.length() + 1;

        JSONObject newTask = new JSONObject();
        newTask.put("id", newId);
        newTask.put("description", description);
        newTask.put("status", "todo"); // Estado por defecto
        newTask.put("createdAt", createdAt);
        newTask.put("updatedAt", ""); // No ha sido actualizada

        // Agregar la nueva tarea al array
        tasks.put(newTask);

        // Guardar las tareas actualizadas en el archivo
        saveTasks(tasks);

        System.out.println("Tarea añadida con éxito: " + newTask.toString(2));
    }

    // ------------------ Método para cargar las tareas ------------------ //
    private static JSONArray loadTasks() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            return new JSONArray(content);
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo. Creando un nuevo listado...");
            return new JSONArray(); // Si el archivo no existe, se devuelve un array vacío
        }
    }

    // ------------------ Método para guardar las tareas ------------------ //
    private static void saveTasks(JSONArray tasks) {
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            file.write(tasks.toString(2)); // Formato bonito con indentación
            file.flush();
        } catch (IOException e) {
            System.out.println("Error al guardar las tareas.");
        }
    }


    //-------------------- Método para actualizar las tareas -------------------//
    private static void updateTask(Scanner scanner){
        System.out.println("Ingrese el id de la tarea:");
        int id = scanner.nextInt();
        scanner.nextLine();

        JSONArray  tasks = loadTasks();

        boolean encontrada = false;
        for(int i = 0; i < tasks.length(); i++){
            JSONObject task = tasks.getJSONObject(i);
            System.out.println(tasks);
            System.out.println(task);
            if (task.getInt("id") == id){

            System.out.println("Ingrese la nueva descripcion de la tarea");
            String newDescription = scanner.nextLine();

            System.out.println("Ingrese el nuevo estado de la tarea (todo, in-progress, done):");
            String newstatus = scanner.nextLine();

            task.put("description", newDescription);
            task.put("status", newstatus);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updateAt = LocalDateTime.now().format(formatter);
            task.put("updateAt", updateAt);

            saveTasks(tasks);
            System.out.println("Tarea actualizada con éxito: " + task.toString(2));
            encontrada = true;
            break;
            }
        }
        if (!encontrada) {
            System.out.println("No se encontró ninguna tarea con el ID proporcionado.");
        }
    };

        //-------------------- Método para eliminar las tareas -------------------//


    private static void deleteTask(Scanner scanner){
        System.out.println("Ingrese el id de la tarea:");
        int id = scanner.nextInt();
        scanner.nextLine();
        JSONArray  tasks = loadTasks();
        boolean encontrada = false;
        for(int i = 0; i < tasks.length(); i++){
            JSONObject task = tasks.getJSONObject(i);
        
        if (task.getInt("id") == id){
            tasks.remove(i);
            encontrada = true;
            break;
        }
        }
        if (!encontrada) {
            System.out.println("No se encontró ninguna tarea con el ID proporcionado.");
        }
    }

    private static void listTasks() {
        JSONArray tasks = loadTasks(); // Cargar las tareas desde el archivo
        if (tasks.length() == 0) {
            System.out.println("No hay tareas registradas.");
            return;
        }
        System.out.println("\n=== Lista de Tareas ===");
        for(int i = 0; i < tasks.length(); i++){
            JSONObject task = tasks.getJSONObject(i);
            System.out.println("ID: " + task.getInt("id"));
            System.out.println("Descripción: " + task.getString("description"));
            System.out.println("Estado: " + task.getString("status"));
            System.out.println("Creado en: " + task.getString("createdAt"));
            System.out.println("Última actualización: " + (task.getString("updatedAt").isEmpty() ? "No actualizada" : task.getString("updatedAt")));
            System.out.println("----------------------");

        }


    }

}