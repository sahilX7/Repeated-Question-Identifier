from flask import Flask,request,jsonify
import numpy as np
import pickle

import helper

model = pickle.load(open('model.pkl','rb'))
app = Flask(__name__)

@app.route('/')
def index():
    return "PROJECT # 2"

@app.route('/predict',methods=['POST'])

def predict():
    q1 = request.form.get('question1')
    q2 = request.form.get('question2')

    input_query = helper.query_point_creator(q1,q2)
    result = model.predict(input_query)[0]
    return jsonify({'is_duplicate':str(result)})

if __name__ == '__main__':
    app.run(host='0.0.0.0',port=5000)