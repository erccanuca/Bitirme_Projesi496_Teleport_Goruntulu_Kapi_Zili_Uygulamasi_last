package myproject.example.com.myapplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public final class Serializer {
    /**
     * Converts an Object to a byte array.
     *
     * @param object, the Object to serialize.
     * @return the byte array that stores the serialized object.
     */
    public static  byte[] serialize(Object object) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);

            return bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ignored) {
            }
            try {
                bos.close();
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * Converts a byte array to an Object.
     *
     * @param byteArray, a byte array that represents a serialized Object.
     * @return an instance of the Object class.
     */
    public static Object deserialize(byte[] byteArray) {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bis.close();
            } catch (IOException ignored) {
            }
            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
            }
        }
    }
}
