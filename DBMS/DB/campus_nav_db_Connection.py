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
# Building info
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


# Find rooms in a building
cursor.execute("""
    SELECT b.building_name, f.floor_number,
    r.room_number, r.room_name, r.room_type
    FROM room r
    JOIN floor f ON r.floor_id = f.floor_id
    JOIN building b ON f.building_id = b.building_id
    WHERE b.building_id = 2
""")
print()
print("======= ROOMS =======")
rooms = cursor.fetchall()
for row in rooms:
    print(row)


# Search for a room and obtain its route node
cursor.execute("""
    SELECT r.room_id, r.room_number, r.room_name,
    f.floor_number, b.building_name, r.entry_node_id
    FROM room r
    JOIN floor f ON r.floor_id = f.floor_id
    JOIN building b ON f.building_id = b.building_id
    WHERE r.room_number LIKE '%101%'
    OR LOWER(r.room_name) LIKE '%computer%'
""")
print()
print("========= Route_node =========")
Route_node = cursor.fetchall()
for row in Route_node:
    print(row)


# Retrieve verified accessible, available paths
cursor.execute("""
    SELECT p.path_id, p.start_node_id, p.end_node_id,
    p.distance, p.estimated_time, p.is_bidirectional
    FROM navigation_path p
    WHERE p.status = 'OPEN'
    AND p.is_accessible = TRUE
""")
print()
print("========= Accessible_path =========")
Accessible = cursor.fetchall()
for row in Accessible:
    print(row)


# Find unavailable paths
cursor.execute("""
    SELECT p.path_id, n1.node_name AS start_location,
    n2.node_name AS end_location, p.status
    FROM navigation_path p
    JOIN navigation_node n1 ON n1.node_id = p.start_node_id
    JOIN navigation_node n2 ON n2.node_id = p.end_node_id
    WHERE p.status <> 'OPEN'
""")
print()
print("========= Unavailable_path =========")
Unavailable_path = cursor.fetchall()
for row in Unavailable_path:
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


# Route history for a registered user
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


# Close and exit
cursor.close()
db.close()