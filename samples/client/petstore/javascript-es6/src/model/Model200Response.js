/*
 * Swagger Petstore
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 *
 * OpenAPI spec version: 1.0.0
 * Contact: apiteam@swagger.io
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 *
 * Swagger Codegen version: 2.4.19-SNAPSHOT
 *
 * Do not edit the class manually.
 *
 */

import {ApiClient} from '../ApiClient';

/**
 * The Model200Response model module.
 * @module model/Model200Response
 * @version 1.0.0
 */
export class Model200Response {
  /**
   * Constructs a new <code>Model200Response</code>.
   * Model for testing model name starting with number
   * @alias module:model/Model200Response
   * @class
   */
  constructor() {
  }

  /**
   * Constructs a <code>Model200Response</code> from a plain JavaScript object, optionally creating a new instance.
   * Copies all relevant properties from <code>data</code> to <code>obj</code> if supplied or a new instance if not.
   * @param {Object} data The plain JavaScript object bearing properties of interest.
   * @param {module:model/Model200Response} obj Optional instance to populate.
   * @return {module:model/Model200Response} The populated <code>Model200Response</code> instance.
   */
  static constructFromObject(data, obj) {
    if (data) {
      obj = obj || new Model200Response();
      if (data.hasOwnProperty('name'))
        obj.name = ApiClient.convertToType(data['name'], 'Number');
      if (data.hasOwnProperty('class'))
        obj._class = ApiClient.convertToType(data['class'], 'String');
    }
    return obj;
  }
}

/**
 * @member {Number} name
 */
Model200Response.prototype.name = undefined;

/**
 * @member {String} _class
 */
Model200Response.prototype._class = undefined;


