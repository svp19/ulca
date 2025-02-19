from flask_restful import Resource
from repositories import UserManagementRepositories
from models import CustomResponse, Status, post_error
from utilities import UserUtils
from flask import request, jsonify
import config
import logging

log         =   logging.getLogger('file')
userRepo    =   UserManagementRepositories()

class CreateUsers(Resource):

    def post(self):
        body = request.get_json()
        if 'users' not in body or not body['users']:
            return post_error("Data Missing", "users not found", None), 400

        users = body['users']
        log.info("Creation request received for {} user/s".format(len(users)))  
        log.info("User/s validation started")
        for i,user in enumerate(users):
            validity = UserUtils.validate_user_input_creation(user)
            if validity is not None:
                log.info("User validation failed for user{}".format(i+1))
                return validity, 400
        log.info("Users are validated")

        try:
            result = userRepo.create_users(users)
            if result is not None:
                log.info("User creation failed | {}".format(str(result)))
                return result, 400   
            else:
                res = CustomResponse(Status.SUCCESS_USR_CREATION.value, None)
                log.info("User creation successful")
                return res.getresjson(), 200
        except Exception as e:
            log.exception("Exception while creating user records: {}".format(str(e)))
            return post_error("Exception occurred", "Exception while performing user creation:{}".format(str(e)), None), 400


class UpdateUsers(Resource):

    def post(self):
        body = request.get_json()
        if 'users' not in body or not body['users']:
            return post_error("Data Missing", "users not found", None), 400

        users = body['users']
        user_id = None
        user_id=request.headers["x-user-id"]
        log.info("Updation request received for {} user/s".format(len(users)))
        log.info("User/s validation started")
        for i,user in enumerate(users):
            validity = UserUtils.validate_user_input_updation(user)
            if validity is not None:
                log.info("User validation failed for user{}".format(i+1))
                return validity, 400
        log.info("Users are validated")

        try:
            result = userRepo.update_users(users,user_id)
            if result== True:
                log.info("User/s updation successful")
                res = CustomResponse(Status.SUCCESS_USR_UPDATION.value, None)
                return res.getresjson(), 200
            else:
                log.info("User updation failed | {}".format(str(result)))
                return result, 400

        except Exception as e:
            log.exception("Exception while updating user records: " + str(e))
            return post_error("Exception occurred", "Exception while performing user updation:{}".format(str(e)), None), 400


class SearchUsers(Resource):

    def post(self):
        user_ids        =   []
        user_emails     =   []
        role_codes      =   []
        org_codes       =   []
        offset          =   None
        limit_value     =   None
        skip_pagination =   None

        body = request.get_json()
        if "userIDs" in body:
            user_ids    =   body['userIDs']
        if "emails" in body:
            user_emails =   body['emails']
        if "roleCodes" in body:
            role_codes  =   body['roleCodes']
        if "orgCodes" in body:
            org_codes   =   body['orgCodes']
        if "offset" in body:
            offset      =   body['offset']
        if "limit" in body:
            limit_value =   body['limit']      
        if "skip_pagination" in body:
            skip_pagination =   body['skip_pagination']
        
        log.info("User/s search request received | {}".format(str(body)))
        
        if not user_ids and not user_emails and not role_codes and not org_codes and not offset and not limit_value:
            offset      =   config.OFFSET_VALUE
            limit_value =   config.LIMIT_VALUE
        try:
            result = userRepo.search_users(user_ids, user_emails, role_codes,org_codes,offset,limit_value,skip_pagination)
            log.info("User/s search successful")
            if result == None:
                log.info("No users matching the search criterias")
                res = CustomResponse(Status.EMPTY_USR_SEARCH.value, None)
                return res.getresjson(), 200
            res = CustomResponse(Status.SUCCESS_USR_SEARCH.value, result[0],result[1])
            return res.getresjson(), 200
        except Exception as e:
            log.exception("Exception while searching user records: " +str(e))
            return post_error("Exception occurred", "Exception while performing user search:{}".format(str(e)), None), 400


class OnboardUsers(Resource):

    def post(self):
        body = request.get_json()
        if 'users' not in body or not body['users']:
            return post_error("Data Missing", "users not found", None), 400
        users = body['users']
        log.info("Request received for onboarding {} user/s".format(len(users)), MODULE_CONTEXT)
        log.info("User/s validation started")
        for i,user in enumerate(users):
            validity = UserUtils.validate_user_input_creation(user)
            if validity is not None:
                log.info("User validation failed for user{}".format(i+1), MODULE_CONTEXT)
                return validity, 400
            log.info("Users are validated")
        try:
            result = userRepo.onboard_users(users)
            if result is not None:
                log.info("User/s onboarding failed | {}".format(str(result)), MODULE_CONTEXT)
                return result, 400              
            else:
                log.info("User/s onboarding successful")
                res = CustomResponse(Status.SUCCESS_USR_ONBOARD.value, None)
                return res.getresjson(), 200
        except Exception as e:
            log.exception("Exception while creating user records for users on-boarding: " + str(e), MODULE_CONTEXT, e)
            return post_error("Exception occurred", "Exception while performing users on-boarding::{}".format(str(e)), None), 400


class SearchRoles(Resource):

    def get(self):
        try:
            log.info("Request for role search received")
            result = userRepo.get_roles()
            if "errorID" in result:
                log.info("Role search failed")
                return result, 400
            else:
                log.info("Role search successful")
                res = CustomResponse(Status.SUCCESS_ROLE_SEARCH.value, result)
                return res.getresjson(), 200
        except Exception as e:
            log.exception("Exception while searching user records: " +
                          str(e), MODULE_CONTEXT, e)
            return post_error("Exception occurred", "Exception while performing user search::{}".format(str(e)), None), 400


class Health(Resource):
    def get(self):
        response = {"code": "200", "status": "ACTIVE"}
        return jsonify(response)


