"""
Team Name: DataDivers
File Name : campus_nav_db_Connection.py
Team Members:
Asive Mjoli 3943507
Lihle Mabuza 4327587
Sonwabo Sigidi 4457827
Wandile Mtsweni 4202706
Masingita Baloyi 4253292
Lutho Dyudu 4458378
""""

import mysql.connector

db = mysql.connector.connect(
    host="172.21.12.21",
    port=23507,
    user="3943507",
    password="!St3943507",
    database="campus_nav_db"
)

print("Connected successfully!")


# Create cursor
cursor = db.cursor()


# Run query
# Buildings by name
cursor.execute("""
    SELECT building_name, building_id,
    building_code
    FROM building
    ORDER BY building_name
""")

# Print buildings
print()
print("========== Buildings ============")
buildings = cursor.fetchall()
for row in buildings:
    print(row)

#Campus entry points
cursor.execute("""
    SELECT node_id, node_name, node_type
    FROM navigation_node
    WHERE node_type IN ('GATE', 'ENTRANCE')
    ORDER BY node_type, node_name
""")

#Print campus entry points
print()
print("====== Campus entry points ========")
Campus_entrance = cursor.fetchall()
for row in Campus_entrance:
    print(row)

#Select rooms in science building
cursor.execute("""
    SELECT b.building_name, f.floor_number,
           r.room_number, r.room_name, r.room_type
    FROM room r
    INNER JOIN floor f ON f.floor_id = r.floor_id
    INNER JOIN building b ON b.building_id = f.building_id
    WHERE b.building_code = 'SCI01'
    ORDER BY f.floor_number, r.room_number
""")

#Print rooms in science building
print()
print("======= Rooms in science building ======")
Science_rooms = cursor.fetchall()
for row in Science_rooms:
    print(row)

#Find a lecture hall and entry node
cursor.execute("""
    SELECT r.room_number, r.room_name,
           b.building_name, f.floor_number,
           r.entry_node_id
    FROM room r
    JOIN floor f ON f.floor_id = r.floor_id
    JOIN building b ON b.building_id = f.building_id
    WHERE LOWER(r.room_name) LIKE '%computer%'
       OR r.room_number LIKE '%101%'
""")

#Print lecture hall and entry node
print()
print("=====Lecture hall =====")
Lecture_hall = cursor.fetchall()
for row in Lecture_hall:
    print(row)

# Search indoor or outdoor facilities
cursor.execute("""
    SELECT f.facility_name, f.facility_type,
    b.building_name, fl.floor_number, f.entry_node_id
    FROM facility f
    LEFT JOIN building b ON b.building_id = f.building_id
    LEFT JOIN floor fl ON fl.floor_id = f.floor_id
    WHERE LOWER(f.facility_type) LIKE '%toilet%'
""")
print()
print("=========INDOOR/OUTDOOR ========")
Search = cursor.fetchall()
for row in Search:
    print(row)


#Unavailable route segments
cursor.execute("""
    SELECT p.path_id, s.node_name AS start_location,
           e.node_name AS end_location, p.status
    FROM navigation_path p
    JOIN navigation_node s ON s.node_id = p.start_node_id
    JOIN navigation_node e ON e.node_id = p.end_node_id
    WHERE p.status IN ('CLOSED', 'MAINTENANCE')
""")

#Print unavailable route
print()
print("===== Unavailable_path =====")
Unavailable_seg = cursor.fetchall()
for row in Unavailable_seg:
    print(row)

#Saved route history
cursor.execute("""
    SELECT r.route_id, s.node_name AS start_location,
    d.node_name AS destination, r.total_distance,
    r.estimated_time, r.created_at
    FROM route r
    JOIN navigation_node s ON s.node_id = r.start_node_id
    JOIN navigation_node d ON d.node_id = r.destination_node_id
    WHERE r.user_id = 1
    ORDER BY r.created_at DESC
""")
print()
print("======= Route_history =========")
Route_history = cursor.fetchall()
for row in Route_history:
    print(row)

#Path type summary
cursor.execute("""
    SELECT path_type, COUNT(*) AS path_count,
           ROUND(AVG(distance), 2) AS avg_metres,
           ROUND(SUM(distance), 2) AS total_metres
    FROM navigation_path
    GROUP BY path_type
    ORDER BY path_type
""")
#Print path summary
print()
print("======= Path Summary ========")
Path_summ = cursor.fetchall()
for row in Path_summ:
    print(row)

#Room navigation readiness by building
cursor.execute("""
   SELECT b.building_name,
          COUNT(DISTINCT r.room_id) AS room_count,
          COUNT(DISTINCT CASE WHEN r.entry_node_id IS NOT NULL
               THEN r.room_id END) AS routable_rooms
   FROM building b
   LEFT JOIN floor f ON f.building_id = b.building_id
   LEFT JOIN room r ON r.floor_id = f.floor_id
   GROUP BY b.building_id, b.building_name
   HAVING COUNT(DISTINCT r.room_id) > 0
   ORDER BY b.building_name
""")
#Print Room navigation
print()
print("===== Room navigation =====")
Room_nav = cursor.fetchall()
for row in Room_nav:
    print(row)

#Verifired usable graph edge
cursor.execute("""
    SELECT p.path_id, s.node_name AS start_location,
           e.node_name AS end_location,
           p.path_type, p.distance
    FROM navigation_path p
    JOIN navigation_node s ON s.node_id = p.start_node_id
    JOIN navigation_node e ON e.node_id = p.end_node_id
    WHERE p.status = 'OPEN'
      AND p.is_accessible = TRUE
      AND s.campus_id = e.campus_id
      AND NOT EXISTS (
          SELECT 1 FROM building b
          WHERE b.building_id IN (s.building_id, e.building_id)
            AND b.status <> 'OPEN'
      )
    ORDER BY p.path_id
""")
#Print usable edges
print()
print("====== Usable Edges =====")
Usable_edges = cursor.fetchall()
for row in Usable_edges:
    print(row)


#Inter-floor accessibility
cursor.execute("""
    SELECT p.path_id, b.building_name,
           sf.floor_number AS from_floor,
           ef.floor_number AS to_floor,
           p.path_type, p.is_accessible
    FROM navigation_path p
    JOIN navigation_node s ON s.node_id = p.start_node_id
    JOIN navigation_node e ON e.node_id = p.end_node_id
    JOIN building b ON b.building_id = s.building_id
    JOIN floor sf ON sf.floor_id = s.floor_id
    JOIN floor ef ON ef.floor_id = e.floor_id
    WHERE s.building_id = e.building_id
      AND sf.floor_number <> ef.floor_number
      AND p.status = 'OPEN'
      AND p.distance <= (
          SELECT AVG(distance) FROM navigation_path
          WHERE path_type IN ('STAIRS', 'ELEVATOR')
      )
    ORDER BY p.path_id
""")
#Print inter-floor accessibility
print()
print("===== Inter-floor accessibility =====")
Inter_floor = cursor.fetchall()
for row in Inter_floor:
    print(row)

#Verified accessibility metadata
cursor.execute("""
    SELECT p.path_id, p.path_type,
           s.node_name AS start_location,
           COUNT(DISTINCT af.accessibility_id) AS verified_features
    FROM navigation_path p
    JOIN navigation_node s ON s.node_id = p.start_node_id
    LEFT JOIN path_accessibility pa ON pa.path_id = p.path_id
    LEFT JOIN accessibility_feature af
           ON af.accessibility_id = pa.accessibility_id
    WHERE p.status = 'OPEN' AND p.is_accessible = TRUE
    GROUP BY p.path_id, p.path_type, s.node_name
    HAVING COUNT(DISTINCT af.accessibility_id) >= 2
    ORDER BY p.path_id
""")

#Print metadata
print()
print("======== Accessibility Metadata =========")
Accessibility_meta = cursor.fetchall()
for row in Accessibility_meta:
    print(row)

# Close and exit
cursor.close()
db.close()