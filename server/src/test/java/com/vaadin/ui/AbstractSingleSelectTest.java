/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.data.datasource.bov.Person;
import com.vaadin.shared.data.DataCommunicatorClientRpc;
import com.vaadin.ui.AbstractSingleSelect.AbstractSingleSelection;

/**
 * Test for {@link AbstractSingleSelect} and {@link AbstractSingleSelection}
 *
 * @author Vaadin Ltd
 */
public class AbstractSingleSelectTest {

    private PersonListing.AbstractSingleSelection selectionModel;
    private List<Person> selectionChanges;

    private static class PersonListing extends
            AbstractSingleSelect<Person> {
        public PersonListing() {
            setSelectionModel(new SimpleSingleSelection());
        }
    }

    @Before
    public void initListing() {
        listing = new PersonListing();
        listing.setItems(PERSON_A, PERSON_B, PERSON_C);
        selectionModel = listing.getSelectionModel();
        selectionChanges = new ArrayList<>();
        listing.addSelectionListener(e -> selectionChanges.add(e.getValue()));
    }

    public static final Person PERSON_C = new Person("c", 3);
    public static final Person PERSON_B = new Person("b", 2);
    public static final Person PERSON_A = new Person("a", 1);
    public static final String RPC_INTERFACE = DataCommunicatorClientRpc.class
            .getName();
    private PersonListing listing;

    @Test
    public void select() {

        selectionModel.select(PERSON_B);

        assertTrue(selectionModel.getSelectedItem().isPresent());

        assertEquals(PERSON_B, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertTrue(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertEquals(Collections.singleton(PERSON_B), selectionModel
                .getSelectedItems());

        assertEquals(Arrays.asList(PERSON_B), selectionChanges);
    }

    @Test
    public void selectDeselect() {

        selectionModel.select(PERSON_B);
        selectionModel.deselect(PERSON_B);

        assertFalse(selectionModel.getSelectedItem().isPresent());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertTrue(selectionModel.getSelectedItems().isEmpty());

        assertEquals(Arrays.asList(PERSON_B, null), selectionChanges);
    }

    @Test
    public void reselect() {

        selectionModel.select(PERSON_B);
        selectionModel.select(PERSON_C);

        assertEquals(PERSON_C, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Collections.singleton(PERSON_C), selectionModel
                .getSelectedItems());

        assertEquals(Arrays.asList(PERSON_B, PERSON_C), selectionChanges);
    }

    @Test
    public void deselectNoOp() {

        selectionModel.select(PERSON_C);
        selectionModel.deselect(PERSON_B);

        assertEquals(PERSON_C, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Collections.singleton(PERSON_C), selectionModel
                .getSelectedItems());

        assertEquals(Arrays.asList(PERSON_C), selectionChanges);
    }

    @Test
    public void selectTwice() {

        selectionModel.select(PERSON_C);
        selectionModel.select(PERSON_C);

        assertEquals(PERSON_C, selectionModel.getSelectedItem().orElse(null));

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertTrue(selectionModel.isSelected(PERSON_C));

        assertEquals(Collections.singleton(PERSON_C), selectionModel
                .getSelectedItems());

        assertEquals(Arrays.asList(PERSON_C), selectionChanges);
    }

    @Test
    public void deselectTwice() {

        selectionModel.select(PERSON_C);
        selectionModel.deselect(PERSON_C);
        selectionModel.deselect(PERSON_C);

        assertFalse(selectionModel.getSelectedItem().isPresent());

        assertFalse(selectionModel.isSelected(PERSON_A));
        assertFalse(selectionModel.isSelected(PERSON_B));
        assertFalse(selectionModel.isSelected(PERSON_C));

        assertTrue(selectionModel.getSelectedItems().isEmpty());

        assertEquals(Arrays.asList(PERSON_C, null), selectionChanges);
    }

}
