/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.domain;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import java.util.List;

public interface DataSetRepository {

    String save(DataSet dataSet);

    DataSet findById(String dataSetId);

    void removeById(String dataSetId);

    List<DataSet> findAll();
}
