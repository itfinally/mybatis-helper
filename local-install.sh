cd mybatis-helper-parent
mvn clean && mvn install

projects=(
mybatis-core
mybatis-generator
mybatis-generator-spring-boot-starter
mybatis-jpa
mybatis-jpa-spring-boot-starter
mybatis-paging
mybatis-paging-spring-boot-starter
)

for project in ${projects[@]}
do
  cd ../${project}
  mvn clean && mvn install
done