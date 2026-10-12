import mysql.connecter

# Connecting to Database
db = mysql.connector.connect{
    host="172.21.12.21",
    port=23507,
    user="3943507",
    password="!St3943507",
    database="campus_nav_db"
}

#Create curser
cursor = db.cursor()

#Run query
cursor.execute("SELECT * FROM buildings LIMIT 5")


#Get the result
result = cursor.fetchall()


#Display the results
for row in resu
lts:
    print(row[0])

# Close and exit
cursor.close()
db.close()
