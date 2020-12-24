package com.turinglabs.keyconnect.server.repository;

import com.turinglabs.keyconnect.server.repository.entities.TestObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

@ConditionalOnProperty(name = "runtime-mode", havingValue = "test")
public interface TestRepo extends JpaRepository<TestObject, String> {

}
