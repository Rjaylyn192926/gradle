/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.model.internal.manage.schema.extract;

import com.google.common.collect.ImmutableList;
import org.gradle.api.Nullable;
import org.gradle.model.ModelMap;
import org.gradle.model.collection.internal.ChildNodeInitializerStrategyAccessor;
import org.gradle.model.collection.internal.ChildNodeInitializerStrategyAccessors;
import org.gradle.model.collection.internal.ModelMapModelProjection;
import org.gradle.model.internal.core.*;
import org.gradle.model.internal.core.rule.describe.ModelRuleDescriptor;
import org.gradle.model.internal.manage.schema.CollectionSchema;
import org.gradle.model.internal.manage.schema.ModelSchema;
import org.gradle.model.internal.manage.schema.SpecializedMapSchema;
import org.gradle.model.internal.type.ModelType;

import java.util.Collections;
import java.util.List;

public class SpecializedMapNodeInitializerExtractionStrategy extends ModelMapNodeInitializerExtractionStrategy {
    private static final ModelType<ModelMap<?>> MODEL_MAP_MODEL_TYPE = new ModelType<ModelMap<?>>() {
    };

    @Override
    public <T> NodeInitializer extractNodeInitializer(ModelSchema<T> schema) {
        return super.extractNodeInitializer(schema);
    }

    @Override
    protected <T, E> NodeInitializer extractNodeInitializer(CollectionSchema<T, E> schema) {
        if (schema instanceof SpecializedMapSchema) {
            return new SpecializedMapNodeInitializer<T, E>((SpecializedMapSchema<T, E>) schema);
        }
        return null;
    }

    @Override
    public Iterable<ModelType<?>> supportedTypes() {
        return ImmutableList.<ModelType<?>>of(MODEL_MAP_MODEL_TYPE);
    }

    private static class SpecializedMapNodeInitializer<T, E> implements NodeInitializer {
        private final SpecializedMapSchema<T, E> schema;

        public SpecializedMapNodeInitializer(SpecializedMapSchema<T, E> schema) {
            this.schema = schema;
        }

        @Override
        public List<? extends ModelReference<?>> getInputs() {
            return Collections.singletonList(ModelReference.of(NodeInitializerRegistry.class));
        }

        @Override
        public void execute(MutableModelNode modelNode, List<ModelView<?>> inputs) {
            NodeInitializerRegistry nodeInitializerRegistry = (NodeInitializerRegistry) inputs.get(0).getInstance();
            ChildNodeInitializerStrategy<E> childFactory = NodeBackedModelMap.createUsingRegistry(schema.getElementType(), nodeInitializerRegistry);
            modelNode.setPrivateData(ModelType.of(ChildNodeInitializerStrategy.class), childFactory);
        }

        @Nullable
        @Override
        public ModelAction getProjector(ModelPath path, ModelRuleDescriptor descriptor) {
            ChildNodeInitializerStrategyAccessor<E> strategyAccessor = ChildNodeInitializerStrategyAccessors.fromPrivateData();
            Class<? extends T> implementationType = schema.getImplementationType().asSubclass(schema.getType().getConcreteClass());
            return AddProjectionsAction.of(ModelReference.of(path), descriptor,
                new SpecializedModelMapProjection<T, E>(schema.getType(), schema.getElementType(), implementationType, strategyAccessor),
                ModelMapModelProjection.unmanaged(schema.getElementType(), strategyAccessor)
            );
        }
    }
}