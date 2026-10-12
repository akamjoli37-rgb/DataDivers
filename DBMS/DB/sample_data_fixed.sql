-- Campus Navigation System: illustrative sample data (MySQL 8.x)
-- This data comes from Section 10 of the DBMS design document.
-- For development/testing only; not verified geographic data for UWC.
-- IMPORTANT: Run once against freshly created, EMPTY tables where IDs start at 1.
-- If your tables already contain data, do not rerun this file unchanged.

USE campus_nav_db;

START TRANSACTION;

INSERT INTO campus (campus_name, address)
VALUES ('UWC Main Campus (illustrative)', 'Bellville, Cape Town');

INSERT INTO building (campus_id, building_code, building_name, description)
VALUES
    (1, 'LIB01', 'Main Library', 'Illustrative library record'),
    (1, 'SCI01', 'Science Building', 'Illustrative science building record'),
    (1, 'ADM01', 'Administration Building', 'Illustrative administration record');

INSERT INTO floor (building_id, floor_number, floor_name)
VALUES
    (1, 0, 'Ground Floor'),
    (1, 1, 'First Floor'),
    (2, 0, 'Ground Floor'),
    (2, 1, 'First Floor');

INSERT INTO navigation_node
    (campus_id, building_id, floor_id, node_name, node_type)
VALUES
    (1, NULL, NULL, 'Main Gate', 'GATE'),
    (1, 1, 1, 'Library Entrance', 'ENTRANCE'),
    (1, 2, 3, 'Science Entrance', 'ENTRANCE'),
    (1, NULL, NULL, 'Main Walkway Intersection', 'INTERSECTION'),
    (1, 1, 1, 'Library Study Room Door', 'ROOM'),
    (1, 2, 3, 'Science Ground Corridor', 'INTERSECTION'),
    (1, 2, 3, 'Science Ground Stairwell', 'STAIRS'),
    (1, 2, 4, 'Science First Floor Stairwell', 'STAIRS'),
    (1, 2, 3, 'Science Ground Elevator', 'ELEVATOR'),
    (1, 2, 4, 'Science First Floor Elevator', 'ELEVATOR'),
    (1, 2, 4, 'Science Lecture Hall Door', 'ROOM'),
    (1, 1, 1, 'Library Ground Corridor', 'INTERSECTION');

INSERT INTO room
    (floor_id, entry_node_id, room_number, room_name, room_type, capacity)
VALUES
    (1, 5, 'G01', 'Library Study Room', 'STUDY_ROOM', 40),
    (3, NULL, 'G10', 'Chemistry Laboratory', 'LABORATORY', 30),
    (4, 11, '101', 'Computer Science Lecture Hall', 'LECTURE_HALL', 120);

INSERT INTO facility
    (campus_id, building_id, floor_id, entry_node_id,
     facility_name, facility_type, description)
VALUES
    (1, 1, 1, 12, 'Library Ground Floor Toilets', 'TOILET', 'Indoor example'),
    (1, NULL, NULL, 1, 'Visitor Parking', 'PARKING', 'Outdoor example');

INSERT INTO navigation_path
    (start_node_id, end_node_id, distance, estimated_time,
     path_type, is_accessible, is_bidirectional)
VALUES
    (1, 4, 120.00, 90, 'WALKWAY', TRUE, TRUE),
    (4, 2, 50.00, 40, 'WALKWAY', TRUE, TRUE),
    (4, 3, 80.00, 60, 'WALKWAY', TRUE, TRUE),
    (2, 12, 10.00, 10, 'CORRIDOR', TRUE, TRUE),
    (12, 5, 10.00, 10, 'CORRIDOR', TRUE, TRUE),
    (3, 6, 15.00, 12, 'CORRIDOR', TRUE, TRUE),
    (6, 7, 5.00, 5, 'CORRIDOR', TRUE, TRUE),
    (7, 8, 6.00, 35, 'STAIRS', FALSE, TRUE),
    (8, 11, 10.00, 8, 'CORRIDOR', TRUE, TRUE),
    (6, 9, 5.00, 5, 'CORRIDOR', TRUE, TRUE),
    (9, 10, 6.00, 35, 'ELEVATOR', TRUE, TRUE),
    (10, 11, 12.00, 10, 'CORRIDOR', TRUE, TRUE);

INSERT INTO accessibility_feature (accessibility_name, description)
VALUES
    ('Wheelchair Accessible', 'Verified wheelchair-suitable path'),
    ('Ramp Access', 'Path provides a ramp'),
    ('Step-Free Access', 'Path does not require stairs'),
    ('Elevator Access', 'Path traverses a suitable elevator');

INSERT INTO path_accessibility (path_id, accessibility_id)
VALUES
    (11, 3),
    (11, 4),
    (10, 3),
    (12, 3);

COMMIT;
