package com.cursointermedio.myapplication.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.R
import androidx.room.Relation
import com.cursointermedio.myapplication.domain.model.DetailModel
import com.cursointermedio.myapplication.domain.model.ExerciseModel
import com.cursointermedio.myapplication.domain.model.RoutineModel
import com.cursointermedio.myapplication.domain.model.TrainingModel
import com.cursointermedio.myapplication.domain.model.WeekModel
import java.util.Date

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
data class TrainingsWithWeekAndRoutineCounts(
    @Embedded val training: TrainingEntity,
    val numWeeks: Int,
    val numRoutines: Int
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

fun WeekModel.toDatabase() = WeekEntity(null, trainingWeekId!!, name, description)


data class WeekWithRoutines(
    @Embedded val week: WeekEntity,
    @Relation(
        parentColumn = "weekId",
        entityColumn = "weekRoutineId"
    )
    val routineList: List<RoutineEntity>
)

data class ExerciseAndDatesCountFromRoutine(
    val numExercise: Int,
    val date: Date
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
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(defaultValue = "0", name = "order") val order: Int?

)

fun RoutineModel.toDatabase() = RoutineEntity(routineId, weekRoutineId, name, description, order)


@Entity(
    tableName = "RoutineExerciseCrossRef",
    primaryKeys = ["routineId", "exerciseId"],  // Aquí estamos creando una clave primaria compuesta
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routineId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoutineExerciseCrossRef(
    val routineId: Long,
    val exerciseId: Long,
    @ColumnInfo(defaultValue = "0", name = "order")val order:Int?
)

data class RoutineWithExercises(
    @Embedded val routine: RoutineEntity,

    @Relation(
        parentColumn = "routineId",
        entityColumn = "exerciseId",
        associateBy = Junction(RoutineExerciseCrossRef::class),
    )
    val exercises: List<ExerciseEntity>
)

data class RoutineWithOrderedExercises(
    val routine: RoutineEntity,
    val exercises: List<ExerciseEntity>
)

@Entity(
    tableName = "exercise_table", foreignKeys = [androidx.room.ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = kotlin.arrayOf("categoryId"),
        childColumns = kotlin.arrayOf("categoryExerciseId"),
        onDelete = androidx.room.ForeignKey.SET_DEFAULT
    )]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exerciseId") val exerciseId: Long?,
    @ColumnInfo(name = "key") val key: String?,
    @ColumnInfo(defaultValue = "8", name = "categoryExerciseId")
    val categoryExerciseId: Long?,
    @ColumnInfo(name = "name") val name: String?,
)

fun ExerciseModel.toDatabase() = ExerciseEntity(
    null,
    key,
    categoryExerciseId = categoryExerciseId,
    name = name
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

data class ExerciseDetailsCount(
    val exerciseId: Long,
    val detailsCount: Int
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

@Entity(
    tableName = "date_table",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routineId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["routineId"], unique = true)]
)
data class DateEntity(
    @PrimaryKey val dateId: String,
    val note: String?,
    val bodyWeight: Float?,
    val routineId: Long?
)

@Entity(
    tableName = "trac_table",
    foreignKeys = [
        ForeignKey(
            entity = DateEntity::class,
            parentColumns = ["dateId"],
            childColumns = ["dateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["dateId"], unique = true)]
)
data class TracEntity(
    @PrimaryKey(autoGenerate = true)
    val tracId: Int = 0,
    val dateId: String,
    val leg: Int?,
    val push: Int?,
    val pull: Int?,
    val rest: Int?,
    val recuperation: Int?,
    val motivation: Int?,
    val technique: Int?
)
data class DateWithTrac(
    @Embedded val dateEntity: DateEntity,
    @Relation(
        parentColumn = "dateId",
        entityColumn = "dateId"
    )
    val tracEntity: TracEntity?
)