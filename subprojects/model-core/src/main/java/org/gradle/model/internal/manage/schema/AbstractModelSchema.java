/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.model.internal.manage.schema;

import com.google.common.base.Function;
import net.jcip.annotations.ThreadSafe;
import org.gradle.api.Nullable;
import org.gradle.model.internal.core.NodeInitializer;
import org.gradle.model.internal.manage.schema.extract.ModelSchemaAspect;
import org.gradle.model.internal.type.ModelType;

import java.util.List;

@ThreadSafe
public class AbstractModelSchema<T> implements ModelSchema<T> {

    private final ModelType<T> type;
    private final Kind kind;

    public static <T> AbstractModelSchema<T> value(ModelType<T> type) {
        return new AbstractModelSchema<T>(type, Kind.VALUE);
    }

    public static <T> ModelManagedImplStructSchema<T> struct(ModelType<T> type, Iterable<ModelProperty<?>> properties, List<ModelSchemaAspect> aspects, Class<? extends T> managedImpl, @Nullable Class<?> delegateType, Function<? super ModelManagedImplStructSchema<T>, NodeInitializer> nodeInitializer) {
        return new ModelManagedImplStructSchema<T>(type, properties, aspects, managedImpl, delegateType, nodeInitializer);
    }

    public static <T> ModelUnmanagedImplStructSchema<T> unmanaged(final ModelType<T> type, Iterable<ModelProperty<?>> properties, List<ModelSchemaAspect> aspects) {
        return new ModelUnmanagedImplStructSchema<T>(type, properties, aspects);
    }

    public static <T, E> ModelCollectionSchema<T, E> collection(ModelType<T> type, ModelType<E> elementType, Function<ModelCollectionSchema<T, E>, NodeInitializer> nodeInitializer) {
        return new ModelCollectionSchema<T, E>(type, elementType, nodeInitializer);
    }

    public static <T> ModelMapSchema<T> specializedMap(ModelType<T> type, ModelType<?> elementType, Class<?> managedImpl) {
        return new ModelMapSchema<T>(type, elementType, managedImpl);
    }

    protected AbstractModelSchema(ModelType<T> type, Kind kind) {
        this.type = type;
        this.kind = kind;
    }

    @Override
    // intended to be overridden
    public NodeInitializer getNodeInitializer() {
        throw new UnsupportedOperationException("Don't know how to create model element from schema for " + type);
    }

    @Override
    public ModelType<T> getType() {
        return type;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return kind.toString().toLowerCase() + " " + type;
    }
}