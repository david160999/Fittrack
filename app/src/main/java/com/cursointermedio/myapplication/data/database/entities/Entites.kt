package com.cursointermedio.myapplication.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel

@Entity(tableName = "training_table")
data class TrainingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trainingId") val trainingId: Long?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?
)

fun TrainingModel.toDatabase() = TrainingEntity(trainingId, name, description)

data class TrainingWithWeeksAndRoutines(
    @Embedded val training: TrainingEntity,
    @Relation(
        entity = WeekEntity::class,
        parentColumn = "trainingId",
        entityColumn = "trainingWeekId"
    )
    val weekWithRoutinesList: List<WeekWithRoutines>
)

data class TrainingWithWeeks(
    @Embedded val training: TrainingEntity,
    @Relation(
        parentColumn = "trainingId",
        entityColumn = "trainingWeekId"
    )
    val weekList: List<WeekEntity>
)


@Entity(
    tableName = "week_table", foreignKeys = [ForeignKey(
        entity = TrainingEntity::class,
        parentColumns = arrayOf("trainingId"),
        childColumns = arrayOf("trainingWeekId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class WeekEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "weekId") val weekId: Long?,
    @ColumnInfo(name = "trainingWeekId") val trainingWeekId: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?
)

fun WeekModel.toDatabase() = WeekEntity(null, trainingWeekId, name, description)


data class WeekWithRoutines(
    @Embedded val week: WeekEntity,
    @Relation(
        parentColumn = "weekId",
        entityColumn = "weekRoutineId"
    )
    val routineList: List<RoutineEntity>
)


@Entity(
    tableName = "routine_table", foreignKeys = [ForeignKey(
        entity = WeekEntity::class,
        parentColumns = arrayOf("weekId"),
        childColumns = arrayOf("weekRoutineId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routineId") val routineId: Long?,
    @ColumnInfo(name = "weekRoutineId") val weekRoutineId: Long,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?
)

fun RoutineModel.toDatabase() = RoutineEntity(null, weekRoutineId, name, description)


@Entity(primaryKeys = ["routineId", "exerciseId"])
data class RoutineExerciseCrossRef(
    val routineId: Long,
    val exerciseId: Long
)

data class RoutineWithExercises(
    @Embedded val routine: RoutineEntity,

    @Relation(
        parentColumn = "routineId",
        entityColumn = "exerciseId",
        associateBy = Junction(RoutineExerciseCrossRef::class)
    )
    val exercises: List<ExerciseEntity>
)

@Entity(
    tableName = "exercise_table", foreignKeys = [androidx.room.ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = kotlin.arrayOf("categoryId"),
        childColumns = kotlin.arrayOf("categoryExerciseId"),
        onDelete = androidx.room.ForeignKey.SET_NULL
    )]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exerciseId") val exerciseId: Long?,
    @ColumnInfo(name = "categoryExerciseId") val categoryExerciseId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?
)

fun ExerciseModel.toDatabase() = ExerciseEntity(null,
    categoryExerciseId = categoryExerciseId,
    name = name,
    description = description
)

data class CategoryWithExercises(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryExerciseId"
    )
    val exerciseList: List<ExerciseEntity>
)

@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoryId") val categoryId: Long,
    @ColumnInfo(name = "name") val name: String,
)


@Entity(
    tableName = "details_table", foreignKeys = [ForeignKey(
        entity = RoutineEntity::class,
        parentColumns = arrayOf("routineId"),
        childColumns = arrayOf("routineDetailsId"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = arrayOf("exerciseId"),
        childColumns = arrayOf("exerciseDetailsId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class DetailsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "detailsId") val detailsId: Long?,
    @ColumnInfo(name = "routineDetailsId") val routineDetailsId: Long,
    @ColumnInfo(name = "exerciseDetailsId") val exerciseDetailsId: Long,
    @ColumnInfo(name = "realWeight") val realWeight: Int?,
    @ColumnInfo(name = "realReps") val realReps: Int?,
    @ColumnInfo(name = "realRpe") val realRpe: Int?,
    @ColumnInfo(name = "objWeight") val objWeight: Int?,
    @ColumnInfo(name = "objReps") val objReps: Int?,
    @ColumnInfo(name = "objRpe") val objRpe: Int?
)

fun DetailModel.toDatabase() = DetailsEntity(
    detailsId,
    routineDetailsId,
    exerciseDetailsId,
    realWeight,
    realReps,
    realRpe,
    objWeight,
    objReps,
    objRpe
)



