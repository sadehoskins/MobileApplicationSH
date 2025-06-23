package com.example.myapplicationtestsade.data.mappers

import com.example.myapplicationtestsade.data.database.entities.UserEntity
import com.example.myapplicationtestsade.data.models.*

/**
 * ******************** USER MAPPER ********************
 * Utility functions to convert between API models and database entities
 * Handles the mapping between RandomUser (API) and UserEntity (Database)
 * Ensures data consistency across different layers
 */
object UserMapper {

    /**
     * Convert RandomUser (API model) to UserEntity (Database model)
     * Generate uniqueId here
     */
    fun toEntity(randomUser: RandomUser): UserEntity {
        return UserEntity(
            // Generate uniqueId safely here
            uniqueId = "${randomUser.login.uuid}_${System.currentTimeMillis()}",

            // ******************** BASIC INFO ********************
            gender = randomUser.gender,
            email = randomUser.email,
            phone = randomUser.phone,
            cell = randomUser.cell,
            nat = randomUser.nat,

            // ******************** NAME (FLATTENED) ********************
            nameTitle = randomUser.name.title,
            nameFirst = randomUser.name.first,
            nameLast = randomUser.name.last,

            // ******************** LOCATION (FLATTENED) ********************
            locationStreetNumber = randomUser.location.street.number,
            locationStreetName = randomUser.location.street.name,
            locationCity = randomUser.location.city,
            locationState = randomUser.location.state,
            locationCountry = randomUser.location.country,
            locationPostcode = randomUser.location.postcode,
            locationCoordinatesLatitude = randomUser.location.coordinates.latitude,
            locationCoordinatesLongitude = randomUser.location.coordinates.longitude,
            locationTimezoneOffset = randomUser.location.timezone.offset,
            locationTimezoneDescription = randomUser.location.timezone.description,

            // ******************** DATES ********************
            dobDate = randomUser.dob.date,
            dobAge = randomUser.dob.age,
            registeredDate = randomUser.registered.date,
            registeredAge = randomUser.registered.age,

            // ******************** LOGIN INFO ********************
            loginUuid = randomUser.login.uuid,
            loginUsername = randomUser.login.username,
            loginPassword = randomUser.login.password,
            loginSalt = randomUser.login.salt,
            loginMd5 = randomUser.login.md5,
            loginSha1 = randomUser.login.sha1,
            loginSha256 = randomUser.login.sha256,

            // ******************** ID (NULLABLE) ********************
            idName = randomUser.id.name,
            idValue = randomUser.id.value,

            // ******************** PICTURES ********************
            pictureLarge = randomUser.picture.large,
            pictureMedium = randomUser.picture.medium,
            pictureThumbnail = randomUser.picture.thumbnail,

            // ******************** METADATA ********************
            isFromApi = true
        )
    }

    /**
     * Convert UserEntity (Database model) to RandomUser (API model)
     */
    fun toRandomUser(entity: UserEntity): RandomUser {
        return RandomUser(
            gender = entity.gender,
            name = Name(
                title = entity.nameTitle,
                first = entity.nameFirst,
                last = entity.nameLast
            ),
            location = Location(
                street = Street(
                    number = entity.locationStreetNumber,
                    name = entity.locationStreetName
                ),
                city = entity.locationCity,
                state = entity.locationState,
                country = entity.locationCountry,
                postcode = entity.locationPostcode,
                coordinates = Coordinates(
                    latitude = entity.locationCoordinatesLatitude,
                    longitude = entity.locationCoordinatesLongitude
                ),
                timezone = Timezone(
                    offset = entity.locationTimezoneOffset,
                    description = entity.locationTimezoneDescription
                )
            ),
            email = entity.email,
            login = Login(
                uuid = entity.loginUuid,
                username = entity.loginUsername,
                password = entity.loginPassword,
                salt = entity.loginSalt,
                md5 = entity.loginMd5,
                sha1 = entity.loginSha1,
                sha256 = entity.loginSha256
            ),
            dob = DateOfBirth(
                date = entity.dobDate,
                age = entity.dobAge
            ),
            registered = Registered(
                date = entity.registeredDate,
                age = entity.registeredAge
            ),
            phone = entity.phone,
            cell = entity.cell,
            id = Id(
                name = entity.idName,
                value = entity.idValue
            ),
            picture = Picture(
                large = entity.pictureLarge,
                medium = entity.pictureMedium,
                thumbnail = entity.pictureThumbnail
            ),
            nat = entity.nat
        )
    }

    /**
     * Convert list of RandomUser to list of UserEntity
     */
    fun toEntityList(randomUsers: List<RandomUser>): List<UserEntity> {
        return randomUsers.map { toEntity(it) }
    }

    /**
     * Convert list of UserEntity to list of RandomUser
     */
    fun toRandomUserList(entities: List<UserEntity>): List<RandomUser> {
        return entities.map { toRandomUser(it) }
    }
}