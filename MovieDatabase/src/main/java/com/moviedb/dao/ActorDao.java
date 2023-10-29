package com.moviedb.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.moviedb.models.Actor;

/**
 * Data Access Object for the Actor class.
 */
public class ActorDao {

    /** Filepath to the csv file. */
    private final String path;


    /**
     * Default constructor.
     * Initializes the path to the default CSV file.
     */
    public ActorDao() {
        // TODO: check if filepath exist
        this("../../resources/data/actors.csv");
    }


    /**
     * Constructor that accepts a specific path.
     *
     * @param path The path to the CSV file.
     */
    public ActorDao(String path) {
        // TODO: check if filepath exists
        this.path = path;
    }


    /**
     * Adds new actor to the csv file.
     *
     * @param actor The actor to be added.
     * @throws IOException If there's an error reading or writing the file.
     */
    public void addActor(Actor actor) throws IOException {
        List<Actor> actors = new ArrayList<>();

        // Read all actors from the file
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                int id = Integer.parseInt(values[0]);
                actors.add(new Actor(id, values[1]));
            }
        }

        // Set new ID for the actor and add it to the list
        actor.setId(getNextId(actors));
        actors.add(actor);

        // Write the updated list of actors back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Actor a : actors) {
                bw.write(a.getId() + ";" + a.getName());
                bw.newLine();
            }
        }
    }


    /**
     * Returns the next available ID for a new actor.
     * The ID is based on the highest existing ID + 1.
     *
     * @param actors The list of all actors.
     * @return The next available ID.
     */
    private int getNextId(List<Actor> actors) {
        int max = 0;
        for (Actor actor : actors) {
            if (actor.getId() > max) {
                max = actor.getId();
            }
        }
        return max + 1;
    }


    /**
     * Retrieves an actor from the csv file based on the provided name.
     *
     * @param name The name of the actor to be retrieved.
     * @return An Actor object if found, otherwise null.
     * @throws IOException If there's an error reading the file.
     */
    public Actor getActorByName(String name) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                // Check if the current actor's name matches the provided name
                if (values[1].equalsIgnoreCase(name)) {
                    return new Actor(Integer.parseInt(values[0]), values[1]);
                }
            }
        }
        return null;
    }


    /**
     * Retrieves an actor from the csv file based on the provided ID.
     *
     * @param id The ID of the actor to be retrieved.
     * @return An Actor object if found, otherwise null.
     * @throws IOException If there's an error reading the file.
     */
    public Actor getActorById(int id) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                // Check if the current actor's ID matches the provided ID
                if (Integer.parseInt(values[0]) == id) {
                    return new Actor(id, values[1]);
                }
            }
        }
        return null;
    }
}
