package com.xizeyoupan.remoteclipboard.dao;

import com.xizeyoupan.remoteclipboard.entity.Connection;

public interface ConnectionDao {
    void save(Connection connection);
    Connection getByUsernameAndConnectionId(java.lang.String username, int id);
}
