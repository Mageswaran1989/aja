import matplotlib.pyplot as plt
X = [[6], [8], [10], [14], [18]] # Size of Pizza
y = [[7], [9], [13], [17.5], [18]]  # Price of Pizza
plt.figure()
plt.title('Pizza price plotted against diameter')
plt.xlabel('Diameter in inches')
plt.ylabel('Price in dollars')
plt.plot(X, y, 'k.')
plt.axis([0, 25, 0, 25])
plt.grid(True)
plt.show()


from sklearn.linear_model import LinearRegression
# Training data
X = [[6], [8], [10], [14], [18]] # Size of Pizza 
y = [[7], [9], [13], [17.5], [18]] # Price of Pizza
# Create and fit the model
model = LinearRegression()
model.fit(X, y)
print 'A 12" pizza should cost: $%.2f' % model.predict([12])[0]

