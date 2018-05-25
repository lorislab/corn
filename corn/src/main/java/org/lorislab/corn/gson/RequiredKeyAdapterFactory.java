/*
 * Copyright 2018 lorislab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.corn.gson;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 *
 * @author andrej
 */
public class RequiredKeyAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type){

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                    if (value != null) {

                    Field[] fields = value.getClass().getDeclaredFields();

                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].isAnnotationPresent(Required.class)) {
                            validateNullValue(value.getClass(), value, fields[i]);
                        }
                    }
                }
                delegate.write(out, value);
            }

            private <T> void validateNullValue(Class clazz, T value, Field field) {
                field.setAccessible(true);
                Class t = field.getType();
                Object v = null;
                try {
                    v = field.get(value);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
                // Put your exhastive if checks here
                if (!t.isPrimitive() && v == null) {                    
                    throw new JsonParseException("In the object '" + clazz.getSimpleName() + "' is the attribute '" + field.getName() + "' required!");
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {

                T value = delegate.read(in);
                if (value != null) {
                    Field[] fields = value.getClass().getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i].isAnnotationPresent(Required.class)) {
                            validateNullValue(value.getClass(), value, fields[i]);
                        }
                    }
                }
                return value;
            }

        };
    }
}