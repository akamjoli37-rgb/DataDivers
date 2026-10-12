-- MySQL 8.x-compatible Campus Navigation schema.
-- Four CHECK constraints involving FK columns with cascading referential actions were removed.
-- Validate corresponding business rules in Java or database triggers.

-- Corrected: straight SQL single quotes for all ENUM values and DEFAULT strings.
-- Database name preserved from the uploaded file; run this on a NEW schema only.
USE campus_nav_db;


CREATE TABLE campus (
                        campus_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        campus_name VARCHAR(150) NOT NULL,
                        address VARCHAR(255),
                        latitude DECIMAL(10,7),
                        longitude DECIMAL(10,7)
) ENGINE=InnoDB;
-- Login is optional for general navigation; admin functions require authorization.
CREATE TABLE app_user (
                          user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          role ENUM('STUDENT','STAFF','VISITOR','ADMIN') NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE building (
                          building_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          campus_id BIGINT UNSIGNED NOT NULL,
                          building_code VARCHAR(20) UNIQUE,
                          building_name VARCHAR(150) NOT NULL,
                          description TEXT,
                          latitude DECIMAL(10,7),
                          longitude DECIMAL(10,7),
                          footprint MULTIPOLYGON SRID 4326 NULL,
                          status ENUM('OPEN','CLOSED','MAINTENANCE') NOT NULL DEFAULT 'OPEN',
                          CONSTRAINT fk_building_campus FOREIGN KEY (campus_id)
                              REFERENCES campus(campus_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE floor (
                       floor_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                       building_id BIGINT UNSIGNED NOT NULL,
                       floor_number INT NOT NULL,
                       floor_name VARCHAR(100),
                       CONSTRAINT uq_building_floor UNIQUE (building_id, floor_number),
                       CONSTRAINT fk_floor_building FOREIGN KEY (building_id)
                           REFERENCES building(building_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Both outdoor and indoor graph vertices belong to a campus.
-- Indoor nodes may also reference a building and floor.
CREATE TABLE navigation_node (
                                 node_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 campus_id BIGINT UNSIGNED NOT NULL,
                                 building_id BIGINT UNSIGNED NULL,
                                 floor_id BIGINT UNSIGNED NULL,
                                 node_name VARCHAR(150),
                                 node_type ENUM('ENTRANCE','ROOM','INTERSECTION','STAIRS','ELEVATOR',
                                     'RAMP','GATE','OUTDOOR') NOT NULL,
                                 latitude DECIMAL(10,7),
                                 longitude DECIMAL(10,7),
                                 x_coordinate DECIMAL(12,2),
                                 y_coordinate DECIMAL(12,2),
                                 location POINT SRID 4326 NULL,
                                 CONSTRAINT chk_node_lat_lon_pair
                                     CHECK ((latitude IS NULL AND longitude IS NULL) OR
                                            (latitude IS NOT NULL AND longitude IS NOT NULL AND
                                             latitude BETWEEN -90 AND 90 AND longitude BETWEEN -180 AND 180)),
                                 CONSTRAINT fk_node_campus FOREIGN KEY (campus_id)
                                     REFERENCES campus(campus_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_node_building FOREIGN KEY (building_id)
                                     REFERENCES building(building_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_node_floor FOREIGN KEY (floor_id)
                                     REFERENCES floor(floor_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Destination node is a navigable doorway/access point, not the room centroid.
CREATE TABLE room (
                      room_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                      floor_id BIGINT UNSIGNED NOT NULL,
                      entry_node_id BIGINT UNSIGNED NULL,
                      room_number VARCHAR(30) NOT NULL,
                      room_name VARCHAR(150),
                      room_type ENUM('CLASSROOM','LECTURE_HALL','LABORATORY','OFFICE',
                          'TOILET','STUDY_ROOM','OTHER') NOT NULL DEFAULT 'OTHER',
                      capacity INT UNSIGNED,
                      status ENUM('AVAILABLE','CLOSED','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
                      CONSTRAINT uq_floor_room UNIQUE (floor_id, room_number),
                      CONSTRAINT fk_room_floor FOREIGN KEY (floor_id)
                          REFERENCES floor(floor_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                      CONSTRAINT fk_room_entry FOREIGN KEY (entry_node_id)
                          REFERENCES navigation_node(node_id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Supports indoor and outdoor facilities; building/floor are nullable.
CREATE TABLE facility (
                          facility_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          campus_id BIGINT UNSIGNED NOT NULL,
                          building_id BIGINT UNSIGNED NULL,
                          floor_id BIGINT UNSIGNED NULL,
                          entry_node_id BIGINT UNSIGNED NULL,
                          facility_name VARCHAR(150) NOT NULL,
                          facility_type VARCHAR(50) NOT NULL,
                          description TEXT,
                          latitude DECIMAL(10,7),
                          longitude DECIMAL(10,7),
                          CONSTRAINT fk_facility_campus FOREIGN KEY (campus_id)
                              REFERENCES campus(campus_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                          CONSTRAINT fk_facility_building FOREIGN KEY (building_id)
                              REFERENCES building(building_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                          CONSTRAINT fk_facility_floor FOREIGN KEY (floor_id)
                              REFERENCES floor(floor_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                          CONSTRAINT fk_facility_entry FOREIGN KEY (entry_node_id)
                              REFERENCES navigation_node(node_id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Paths connect nodes; route calculation happens in the application layer.
-- is_accessible = NULL means unverified, not safely accessible.
CREATE TABLE navigation_path (
                                 path_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                 start_node_id BIGINT UNSIGNED NOT NULL,
                                 end_node_id BIGINT UNSIGNED NOT NULL,
                                 distance DECIMAL(10,2) NOT NULL,
                                 estimated_time INT UNSIGNED,
                                 path_type ENUM('WALKWAY','CORRIDOR','STAIRS','ELEVATOR','RAMP',
                                     'ROAD','CROSSING','DOORWAY') NOT NULL,
                                 is_accessible BOOLEAN NULL DEFAULT NULL,
                                 is_bidirectional BOOLEAN NOT NULL DEFAULT TRUE,
                                 status ENUM('OPEN','CLOSED','MAINTENANCE') NOT NULL DEFAULT 'OPEN',
                                 path_geometry LINESTRING SRID 4326 NULL,
                                 CONSTRAINT chk_path_distance CHECK (distance > 0),
                                 CONSTRAINT fk_path_start FOREIGN KEY (start_node_id)
                                     REFERENCES navigation_node(node_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_path_end FOREIGN KEY (end_node_id)
                                     REFERENCES navigation_node(node_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE accessibility_feature (
                                       accessibility_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                       accessibility_name VARCHAR(100) NOT NULL UNIQUE,
                                       description TEXT
) ENGINE=InnoDB;

CREATE TABLE path_accessibility (
                                    path_id BIGINT UNSIGNED NOT NULL,
                                    accessibility_id BIGINT UNSIGNED NOT NULL,
                                    PRIMARY KEY (path_id, accessibility_id),
                                    CONSTRAINT fk_pa_path FOREIGN KEY (path_id)
                                        REFERENCES navigation_path(path_id) ON UPDATE CASCADE ON DELETE CASCADE,
                                    CONSTRAINT fk_pa_accessibility FOREIGN KEY (accessibility_id)
                                        REFERENCES accessibility_feature(accessibility_id)
                                        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Optional saved route; normal navigation does not require persistence.
-- Saved routes must be revalidated against current path/closure status.
CREATE TABLE route (
                       route_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT UNSIGNED NULL,
                       start_node_id BIGINT UNSIGNED NOT NULL,
                       destination_node_id BIGINT UNSIGNED NOT NULL,
                       total_distance DECIMAL(10,2),
                       estimated_time INT UNSIGNED,
                       accessible_route BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_route_user FOREIGN KEY (user_id)
                           REFERENCES app_user(user_id) ON UPDATE CASCADE ON DELETE SET NULL,
                       CONSTRAINT fk_route_start FOREIGN KEY (start_node_id)
                           REFERENCES navigation_node(node_id) ON UPDATE CASCADE ON DELETE RESTRICT,
                       CONSTRAINT fk_route_destination FOREIGN KEY (destination_node_id)
                           REFERENCES navigation_node(node_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
CREATE TABLE route_segment (
                               route_segment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               route_id BIGINT UNSIGNED NOT NULL,
                               path_id BIGINT UNSIGNED NOT NULL,
                               segment_order INT UNSIGNED NOT NULL,
                               traversal ENUM('FORWARD','REVERSE') NOT NULL DEFAULT 'FORWARD',
                               CONSTRAINT uq_route_segment_order UNIQUE (route_id, segment_order),
                               CONSTRAINT chk_segment_order CHECK (segment_order > 0),
                               CONSTRAINT fk_segment_route FOREIGN KEY (route_id)
                                   REFERENCES route(route_id) ON UPDATE CASCADE ON DELETE CASCADE,
                               CONSTRAINT fk_segment_path FOREIGN KEY (path_id)
                                   REFERENCES navigation_path(path_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
