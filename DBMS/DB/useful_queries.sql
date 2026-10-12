-- CSC312 Practical Assignment 2 - twelve queries (2 simple, 6 medium, 4 complex)
-- Assumes the illustrative Section 10 fixture has been loaded.
-- Use the configured development database.

-- Q01 Buildings by name
-- Purpose: Provides an alphabetical directory of campus buildings.
SELECT building_id, building_code, building_name
FROM building
ORDER BY building_name;

-- Q02 Campus entry points
-- Purpose: Lists gates and entrances for starting a journey.
SELECT node_id, node_name, node_type
FROM navigation_node
WHERE node_type IN ('GATE', 'ENTRANCE')
ORDER BY node_type, node_name;

-- Q03 Rooms in Science Building
-- Purpose: Joins three tables to identify rooms by building and floor.
SELECT b.building_name, f.floor_number,
       r.room_number, r.room_name, r.room_type
FROM room r
         INNER JOIN floor f ON f.floor_id = r.floor_id
         INNER JOIN building b ON b.building_id = f.building_id
WHERE b.building_code = 'SCI01'
ORDER BY f.floor_number, r.room_number;

-- Q04 Find a lecture hall and entry node
-- Purpose: Uses joins and text filtering to link a searched room to its route anchor.
SELECT r.room_number, r.room_name,
       b.building_name, f.floor_number,
       r.entry_node_id
FROM room r
         JOIN floor f ON f.floor_id = r.floor_id
         JOIN building b ON b.building_id = f.building_id
WHERE LOWER(r.room_name) LIKE '%computer%'
   OR r.room_number LIKE '%101%';

-- Q05 Find indoor toilets
-- Purpose: Uses LEFT JOINs so indoor/outdoor facilities remain searchable.
SELECT fa.facility_name, fa.facility_type,
       COALESCE(b.building_name, 'Outdoor') AS location,
       fl.floor_number, fa.entry_node_id
FROM facility fa
         LEFT JOIN building b ON b.building_id = fa.building_id
         LEFT JOIN floor fl ON fl.floor_id = fa.floor_id
WHERE LOWER(fa.facility_type) LIKE '%toilet%';

-- Q06 Unavailable route segments
-- Purpose: Uses joins and status filters to identify closures and maintenance.
SELECT p.path_id, s.node_name AS start_location,
       e.node_name AS end_location, p.status
FROM navigation_path p
         JOIN navigation_node s ON s.node_id = p.start_node_id
         JOIN navigation_node e ON e.node_id = p.end_node_id
WHERE p.status IN ('CLOSED', 'MAINTENANCE');

-- Q07 Saved route history
-- Purpose: Uses joins, filtering, and descending timestamps for optional history.
SELECT r.route_id, s.node_name AS start_location,
       e.node_name AS destination, r.total_distance,
       r.estimated_time, r.created_at
FROM route r
         JOIN navigation_node s ON s.node_id = r.start_node_id
         JOIN navigation_node e ON e.node_id = r.destination_node_id
WHERE r.user_id = 1
ORDER BY r.created_at DESC;

-- Q08 Path type summary
-- Purpose: Aggregates route distance and number of segments by path type.
SELECT path_type, COUNT(*) AS path_count,
       ROUND(AVG(distance), 2) AS avg_metres,
       ROUND(SUM(distance), 2) AS total_metres
FROM navigation_path
GROUP BY path_type
ORDER BY path_type;

-- Q09 Room navigation readiness by building
-- Purpose: Combines left joins, conditional aggregation, GROUP BY, and HAVING.
SELECT b.building_name,
       COUNT(DISTINCT r.room_id) AS room_count,
       COUNT(DISTINCT CASE WHEN r.entry_node_id IS NOT NULL
                               THEN r.room_id END) AS routable_rooms
FROM building b
         LEFT JOIN floor f ON f.building_id = b.building_id
         LEFT JOIN room r ON r.floor_id = f.floor_id
GROUP BY b.building_id, b.building_name
HAVING COUNT(DISTINCT r.room_id) > 0
ORDER BY b.building_name;

-- Q10 Verified usable graph edges
-- Purpose: Joins endpoints and excludes closed buildings using a correlated subquery.
SELECT p.path_id, s.node_name AS start_location,
       e.node_name AS end_location,
       p.path_type, p.distance
FROM navigation_path p